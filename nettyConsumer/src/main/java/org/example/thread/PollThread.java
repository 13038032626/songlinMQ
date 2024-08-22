package org.example.thread;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.example.handler.HttpResponseHandler;
import org.example.handler.PollHandler;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class PollThread extends Thread{
    private static final String SERVER_URL = "http://localhost:8080/poll"; // Server URL
    private static final int POLL_INTERVAL_MS = 5000; // Poll interval in milliseconds
    private final ChannelHandlerContext context;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public PollThread(ChannelHandlerContext nettyContext) {
        this.context = nettyContext;
    }
    @Override
    public void run() {
        // 以 long polling 从server拉取，拉回带的再投入handler
        while (running.get()){
            try {
                // 发起 HTTP 请求，拉取消息
                HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, SERVER_URL);
                HttpHeaders headers = request.headers();
                headers.set(CONTENT_TYPE, "application/json");
                headers.set(CONTENT_LENGTH, 0);

                // 使用 Netty 客户端发起请求
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(new NioEventLoopGroup())
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpClientCodec());
                                pipeline.addLast(new HttpObjectAggregator(8192));
                                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                                pipeline.addLast(new PollHandler());
                                pipeline.addLast(new HttpResponseHandler(context));
                            }
                        });

                ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
                future.channel().writeAndFlush(request).sync();
                // 消费者角度，长轮询和轮询没有区别，只管发即可
                // 关键操作是在broker将请求挂起，有足够消息再返回

                future.channel().closeFuture().sync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
