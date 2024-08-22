package org.example.fail;

import org.example.message.datas.Data;
import org.example.queue.DeadMessageQueue;

import java.util.Queue;

public class QueueDispatchFail {
    static DeadMessageQueue queue = new DeadMessageQueue();

    public void add(Data m){
        queue.add(m);
    }

    public static Queue<Data> getQueue() {
        return queue.getDataQueue();
    }
}
