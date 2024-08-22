package org.example;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

public class ClientUI {
    public ClientUI(Channel channel){
        JFrame frame = new JFrame("测试Swing");
        JPanel panel = new JPanel();
        JButton button1 = new JButton("发送消息");
        JTextField textField = new JFormattedTextField();
        button1.addActionListener(e -> {
            String actionCommand = e.getActionCommand();
            if(actionCommand.equals("发送消息")){
                if(/*延迟发送*/true){
                    new Thread(()->{
                        try {
                            Thread.sleep(2000);

                        String text = textField.getText();
                        int times = 0;
                        while (/*锁是被占用的*/true) {
                            ChannelFuture channelFuture = channel.writeAndFlush(Unpooled.copiedBuffer(text.getBytes()));
                            Thread.sleep(1000);
                        }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).start();
                }
                String text = textField.getText();
                channel.writeAndFlush(Unpooled.copiedBuffer(text.getBytes()));
            }
        });
        textField.setColumns(10);
        panel.add(button1);
        panel.add(textField);
        frame.add(panel);
        frame.setSize(new Dimension(500,200));
        frame.setVisible(true);
    }
}
