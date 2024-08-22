package org.example.queue.SegmentQueue;

import org.example.lib.redisLike.Listpack;
import org.example.lib.redisLike.RadixTree;
import org.example.message.datas.Data;
import org.example.message.datas.TextMessage;

import java.util.ArrayList;
import java.util.List;

public class RedisLikeSegmentQueue implements BaseSegmentQueue{

    private final RadixTree radixTree;
    private final Listpack listpack;
    private long lastTimestamp;

    public RedisLikeSegmentQueue() {
        this.radixTree = new RadixTree();
        this.listpack = new Listpack();
        this.lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public void push(String message) {
        String id = generateMessageId();
        listpack.addMessage(message);
        radixTree.add(id,message);  // 将消息ID映射到Listpack中的索引位置
    }

    @Override
    public Data get() {
        String id = radixTree.get(String.valueOf(lastTimestamp));
        if (id == null) {
            return null; // 无可获取的消息
        }

        int messageIndex = Integer.parseInt(radixTree.get(id));
        String message = listpack.getMessage(messageIndex);

        return new TextMessage(id, message);
    }

    @Override
    public boolean isFull() {
        return listpack.isFull();
    }

    private String generateMessageId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp == lastTimestamp) {
            timestamp++;  // 确保唯一性
        }
        lastTimestamp = timestamp;
        return String.valueOf(timestamp);
    }
}