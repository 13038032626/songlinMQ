package org.example.thread;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.example.queue.MyQueue;
import org.example.handler.DemoSocketServerHandler;

import java.util.List;
import java.util.Random;

public class AutoSendThread implements Runnable {

    List<Channel> consumerChannels = DemoSocketServerHandler.consumerChannels;

    Integer exchangeType = 1; //1是点对点，2是发布订阅

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }

    @Override
    public void run() {
        while (true) {
            String message = MyQueue.getPush();
            if (exchangeType == 1) {
                if(consumerChannels.size() == 0){
                    continue;//放弃这条消息
                }
                Random random = new Random();
                int i = random.nextInt(0, consumerChannels.size());
                Channel targetChannel = consumerChannels.get(i);
                System.out.println(message+"  消息发送到： "+targetChannel);
                targetChannel.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
            } else if (exchangeType == 2) {
                for (Channel c: consumerChannels) {
                    c.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
                }
            }
        }
    }
}
