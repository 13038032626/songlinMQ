package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.example.ui.ConsumerUI;
import org.example.handler.PollHandler;
import org.example.handler.PushHandler;
import org.example.handler.UIHandler;
import org.example.thread.PollThread;

import java.io.IOException;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        String type = "poll";
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            if(type.equals("poll")){
                                pipeline.addLast(new PollHandler());
                            }else {
                                pipeline.addLast(new PushHandler());
                            }
                            UIHandler uiHandler = new UIHandler();
                            ConsumerUI consumerUI = new ConsumerUI();
                            uiHandler.setTextField(consumerUI.getTextField());
                            pipeline.addLast(uiHandler);
                        }
                    });

            ChannelFuture future = bootstrap.connect("192.168.56.10", 8890).sync();
            Channel channel = future.channel();
            if(type.equals("poll")){
                new PollThread(channel.pipeline().lastContext()).start();
            }
            channel.closeFuture().sync();
            System.out.println("channel关闭完成");
        } finally {
            if(eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully();
            }
        }
    }
}
