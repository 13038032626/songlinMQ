package org.example.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

class QueueWithDetails{
    public BlockingQueue<String> queue;

    public String description;

    public QueueWithDetails(BlockingQueue<String> queue, String description) {
        this.queue = queue;
        this.description = description;
    }
    public int getDescriptionSize = (description+":"+queue.size()).length();

    @Override
    public String toString() {
        return description+":"+queue.size();
    }
}

public class MyQueue {

    public static List<QueueWithDetails> pushQueue = new ArrayList<>();

    public static List<PollQueue> pollQueue = new ArrayList<>();

    public static void addPush(Object msg,Integer index){

        try {
            pushQueue.get(index).queue.put(((String) msg));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public static String getPush(Integer index){
        try {
            return pushQueue.get(index).queue.take();
        } catch (InterruptedException e) {
//            System.out.println("队列已空，无法取出");
            Thread.currentThread().interrupt();
            return "默认值";
        }
    }
    public static void addPoll(String msg,Integer index){
        try {
            pollQueue.get(index).queue.put(((String) msg));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public static String getPoll(Integer index){
        try {
            return pollQueue.get(index).queue.take();
        } catch (InterruptedException e) {
//            System.out.println("队列已空，无法取出");
            Thread.currentThread().interrupt();
            return "默认值";
        }
    }
    public static String details() {

        StringBuilder sb = new StringBuilder();
        sb.append("poll:");
        sb.append(2); //作用是空出一个int的长度，等待填充poll的整体长度
        int pollSize = 0;
        for (int i = 0; i < pollQueue.size(); i++) {
            pollSize+=pollQueue.get(i).getDescriptionSize;
            sb.append(pollQueue.get(i).description);
        }
        sb.replace(6,10,String.valueOf(pollSize));

        sb.append("push:");
        sb.append(2);
        int pushSize = 0;
        for (int i = 0; i < pushQueue.size(); i++) {
            pollSize+=pushQueue.get(i).getDescriptionSize;
            sb.append(pushQueue.get(i).description);
        }
        sb.replace(12+pollSize,16+pollSize,String.valueOf(pushSize));
    return sb.toString();
    }
}
