package org.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.swing.*;

public class UIHandler
        extends ChannelInboundHandlerAdapter {

    JTextField textField;

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        textField.setText((String) msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ctx.channel().writeAndFlush("typeIdentify:consumer");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

