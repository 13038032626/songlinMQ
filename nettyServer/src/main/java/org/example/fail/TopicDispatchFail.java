package org.example.fail;

import org.example.message.datas.Data;
import org.example.queue.DeadMessageQueue;

import java.util.Queue;
import java.util.logging.Logger;

public class TopicDispatchFail{
    private static DeadMessageQueue queue = new DeadMessageQueue();
    private static Logger logger = Logger.getLogger(TopicDispatchFail.class.getName());
    static final int DISMISS_LIMITATION = 20;
    public static void add(Data m){
        if(queue.size() > DISMISS_LIMITATION){
            dismiss();
        }
        queue.add(m);
    }

    public static Queue<Data> getQueue() {
        return queue.getDataQueue();
    }
    private static void dismiss(){
        // 消息在broker中投递失败，当策略是放弃时，记下日志就销毁
        logger.warning("消息丢失"+queue.toString());
        queue.clear();
    }
}
