package org.example.queue;

import org.example.message.datas.Data;

import java.util.ArrayDeque;
import java.util.Queue;

public class DeadMessageQueue {
    private Queue<Data> dataQueue = new ArrayDeque<>();
    public void add(Data message) {
        dataQueue.offer(message);
    }
    public boolean isEmpty() {
        return dataQueue.isEmpty();
    }

    public Queue<Data> getDataQueue() {
        return dataQueue;
    }
    public int size(){
        return dataQueue.size();
    }

    public void clear() {
        dataQueue.clear();
    }
}
