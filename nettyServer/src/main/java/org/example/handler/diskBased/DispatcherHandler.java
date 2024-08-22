package org.example.handler.diskBased;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.fail.TopicDispatchFail;
import org.example.message.datas.TextMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    // 将消息分topic
    private final Map<String, TopicDispatcherHandler> partitions = new HashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        String targetTopic = determineTopic(message);
        if(targetTopic == "fail"){
            return;
        }
        TopicDispatcherHandler topicDispatcher = partitions.get(targetTopic);
        topicDispatcher.channelRead(ctx,message);
    }
    public String determineTopic(String message){
        for (TopicDispatcherHandler topic : partitions.values()) {
            if(Objects.equals(topic.getName(), message.split("/.")[0])){
                return topic.getName();
            }
        }
        // 匹配失败，即将丢失，加入死信等待后续操作
        TopicDispatchFail.add(new TextMessage(message));
        return "fail";
    }
}
