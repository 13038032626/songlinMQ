package org.example.queue.SegmentQueue;

import org.example.message.datas.Data;
import org.example.message.datas.TypeConverter;
public class SimpleSegmentQueue implements BaseSegmentQueue{

    Data[] data = new Data[100];
    int index = 0;
    @Override
    public void push(String message) {
        assert index < 100 : "越界,queue访问超过100";
        data[index++] = TypeConverter.convert(message);
    }

    @Override
    public Data get() {
        assert index > 0 : "越界，访问空queue";
        return data[index-1];
    }

    @Override
    public boolean isFull() {
        return index == 100;
    }
}
