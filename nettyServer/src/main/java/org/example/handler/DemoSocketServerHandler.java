package org.example.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.queue.MyQueue;
import org.example.message.DataMessage;
import java.util.ArrayList;
import java.util.List;


public class DemoSocketServerHandler
        extends ChannelInboundHandlerAdapter {

    public static List<Channel> consumerChannels = new ArrayList<>();

    public static List<Channel> supplierChannels = new ArrayList<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        if(msg instanceof DataMessage){
            ctx.executor().execute(()->{
                //ack
            });
            DataMessage str = (DataMessage) msg;
            //client的channel发起身份认证消息，理论上这应该在channelActive里完成，但那边区分不开
            if(str.data.from.startsWith("typeIdentify:")){
                Channel channel = ctx.channel();
                String substring = str.data.from.substring(13);
                if (substring.equals("consumer")) consumerChannels.add(channel);
                else if(substring.equals("supplier")) supplierChannels.add(channel);
                return;
            }
        }


        MyQueue.addPush(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.writeAndFlush("details "+MyQueue.details());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

