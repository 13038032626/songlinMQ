package org.example.handler.diskBased;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.queue.SegmentQueue.SimpleSegmentQueue;

import javax.swing.text.Segment;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class TopicDispatcherHandler extends ChannelInboundHandlerAdapter {
    // 将一个topic下的消息分partition / segment
    private String name;

    public TopicDispatcherHandler(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    private final Map<String, List<SegmentFile>> partitions = new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        String partitionKey = determinePartitionKey(message);
        SegmentFile targetSegment = getOrCreateSimpleSegmentQueue(partitionKey);
        // partition下拿到可用的segment文件，每个大小约1024 * 1024
        writeToFile(targetSegment.fileName,message);
    }
    public String determinePartitionKey(String message){
        CRC32 crc32 = new CRC32();
        crc32.update(message.getBytes(StandardCharsets.UTF_8));
        long hashValue = crc32.getValue();
        int targetNum = (int) hashValue % partitions.size();
        //todo:暂时写成partition数量不变时，获取第targetNum个分区
        return partitions.keySet().toArray()[targetNum].toString();
    }
    public SegmentFile getOrCreateSimpleSegmentQueue(String partitionKey){
        List<SegmentFile> segments = partitions.computeIfAbsent(partitionKey, k -> new ArrayList<>());
        SegmentFile targetSegment;
        if(segments.isEmpty() || segments.get(segments.size() - 1).isFull()){
            targetSegment = new SegmentFile(System.currentTimeMillis());
            segments.add(targetSegment);
        }else {
            targetSegment = segments.get(segments.size() - 1);
        }
        return targetSegment;
    }
    private void writeToFile(String partitionKey,String message) throws IOException {
        File file = new File(partitionKey + ".log");
        /*
        
         */
        try (RandomAccessFile raf = new RandomAccessFile(file,"rw");
             FileChannel fileChannel = raf.getChannel()) {
            // 优化1：page cache文件映射
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,fileChannel.size(),message.length());

            mappedByteBuffer.put(message.getBytes());
            mappedByteBuffer.force();// 强制刷盘;

            // 优化2：可能使用到零拷贝，java竟然也有API
            /*
            fileChannel.transferFrom 和 transferTo
            消费者读时用to直接将磁盘上记录的文件传入channel
             */
        }

    }
}
