package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import org.example.message.datas.TextMessage;
import org.example.queue.DeadQueue;

import java.util.concurrent.TimeUnit;

public class PollHandler extends ChannelInboundHandlerAdapter {
    // 由PollThread先从server拉来，再投进handler中
    DeadQueue deadQueue;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("拉回来消息" + msg);
        if(deadQueue == null){
            deadQueue = new DeadQueue(ctx);
        }
        // 直接接到来自server的消息
        //---
        /*
        模拟消费情况
        1. 消费成功 - 返回ack
        2. 消费时间过长
        3. 消费失败，中途报异常
         */
        try {
            System.out.println("收到消息"+msg);
            int condition = randomCondition();
            switch (condition){
                case 1:
                    handleSuccessfulResult(ctx,msg);
                    break;
                case 2:
                    handleLongWaitingResult(ctx,msg);
                    break;
                case 3:
                    handleFailedResult(ctx,msg);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }catch (Exception e){
            System.out.println("未正常消费，进入暂存队列");
            deadQueue.add(new TextMessage((String) msg));
        }

    }
    private int randomCondition(){
        return (int) (Math.random() * 3) + 1;
        //1 - 2 2- 3 3 - 4
    }
    private void handleSuccessfulResult(ChannelHandlerContext cxt,Object msg){
        System.out.println("arrived success");
        cxt.writeAndFlush("ack:"+msg);
    }
    private void handleLongWaitingResult(ChannelHandlerContext cxt,Object msg){
        System.out.println("arrived long");
        ScheduledFuture<?> future = cxt.executor().schedule(() -> {
            System.out.println("Message consumed after delay.");
            cxt.writeAndFlush("ACK after delay: " + msg);
        }, 5, TimeUnit.SECONDS); // 模拟 5 秒的延迟
    }
    private void handleFailedResult(ChannelHandlerContext ctx, Object msg) {
        System.out.println("arrived fail");
        throw new RuntimeException("Failed to consume message: " + msg);
    }
}
