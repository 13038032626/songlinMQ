package org.example.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.ScheduledFuture;

import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

@ChannelHandler.Sharable
public class HttpResponseHandler extends ChannelInboundHandlerAdapter {

    ChannelHandlerContext context;
    private static final ConcurrentHashMap<String, Queue<ChannelHandlerContext>> topicMap = new ConcurrentHashMap<>();

    public HttpResponseHandler(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            if (request.uri().contains("/poll")) {
                String topic = extractTopic(request.uri());
                processLongPollingRequest(ctx, topic);
            }
            request.release();
        }
    }
    private void processLongPollingRequest(ChannelHandlerContext ctx, String topic) {
        // 将请求加入挂起队列
        Queue<ChannelHandlerContext> queue = topicMap.computeIfAbsent(topic, k -> new ConcurrentLinkedQueue<>());
        queue.add(ctx);

        // 设置超时任务，防止长时间挂起
        ScheduledFuture<?> timeoutFuture = ctx.executor().schedule(() -> {
            if (queue.remove(ctx)) {
                sendTimeoutResponse(ctx);
            }
        }, 30, TimeUnit.SECONDS);

        // 将超时任务绑定到 context 上，处理连接关闭等情况
        ctx.channel().closeFuture().addListener(future -> timeoutFuture.cancel(false));
    }

    public static void onNewMessage(String topic, String message) {
        Queue<ChannelHandlerContext> queue = topicMap.get(topic);
        if (queue != null) {
            ChannelHandlerContext ctx;
            while ((ctx = queue.poll()) != null) {
                sendResponse(ctx, message);
            }
        }
    }

    private static void sendResponse(ChannelHandlerContext ctx, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                ctx.alloc().buffer().writeBytes(message.getBytes()));

        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(f -> ctx.close());
    }

    private void sendTimeoutResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
        ctx.writeAndFlush(response).addListener(f -> ctx.close());
    }

    private String extractTopic(String uri) {
        // URI "/poll/topicName"
        return uri.substring(uri.lastIndexOf("/") + 1);
    }
}
