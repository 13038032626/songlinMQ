package org.example.queue.SegmentQueue;

import org.example.message.datas.Data;

public interface BaseSegmentQueue {

    public void push(String message);

    public Data get();

    public boolean isFull();
}
