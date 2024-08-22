package org.example.handler.memoryBased;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.queue.SegmentQueue.SimpleSegmentQueue;

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

    private final Map<String, List<SimpleSegmentQueue>> partitions = new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        String partitionKey = determinePartitionKey(message);
        SimpleSegmentQueue targetQueue = getOrCreateSimpleSegmentQueue(partitionKey);
        targetQueue.push(message);
    }
    public String determinePartitionKey(String message){
        CRC32 crc32 = new CRC32();
        crc32.update(message.getBytes(StandardCharsets.UTF_8));
        long hashValue = crc32.getValue();
        int targetNum = (int) hashValue % partitions.size();
        //todo:暂时写成partition数量不变时，获取第targetNum个分区
        return partitions.keySet().toArray()[targetNum].toString();
    }
    public SimpleSegmentQueue getOrCreateSimpleSegmentQueue(String partitionKey){
        List<SimpleSegmentQueue> queues = partitions.computeIfAbsent(partitionKey, k -> new ArrayList<>());
        SimpleSegmentQueue targetQueue;
        if(queues.isEmpty() || queues.get(queues.size() - 1).isFull()){
            targetQueue = new SimpleSegmentQueue();
            queues.add(targetQueue);
        }else {
            targetQueue = queues.get(queues.size() - 1);
        }
        return targetQueue;
    }
}
