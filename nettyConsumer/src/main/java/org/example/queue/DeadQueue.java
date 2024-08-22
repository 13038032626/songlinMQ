package org.example.queue;

import io.netty.channel.ChannelHandlerContext;
import org.example.message.datas.Data;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

public class DeadQueue {
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private Queue<Data> dataQueue = new ArrayDeque<>();
    private static Logger logger = Logger.getLogger(DeadQueue.class.getName());
    private ChannelHandlerContext ctx;
    public DeadQueue(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }
    private static final int PARTITION_SIZE = 20;
    public void add(Data message) {
        if(dataQueue.size() > PARTITION_SIZE) {
            // 执行消费失败逻辑，此处暂定未broker发出后不再负责，无法让broker重发
            // 于是消费失败可以有：
            // 1. 记下日志 + 失败消息存盘
            // 2. 重新投递，设置重试次数上限
            handleFail();
        }
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
    private void handleFail(){
        logger.warning("20个失败消息重新投递");
        saveFailMessage();
        retry();
    }
    private void saveFailMessage(){
        //todo: 存盘
        System.out.println(dataQueue+"已存盘");
    }
    private boolean retry(){
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                for (Data data:dataQueue) {
                    ctx.pipeline().firstContext().fireChannelRead(data);
                }
                return true;
            } catch (Exception e) {
                logger.warning("Retry attempt " + attempt);
            }
        }
        return false;
    }

}
