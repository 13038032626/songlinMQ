package org.example.ui;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;

public class ConsumerUI {
    JTextField textField;
    public ConsumerUI(){
        JFrame frame = new JFrame("测试Swing");
        JPanel panel = new JPanel();
        textField = new JFormattedTextField();

        textField.setColumns(10);
        panel.add(textField);
        frame.add(panel);
        frame.setSize(new Dimension(500,200));
        frame.setVisible(true);
    }

    public JTextField getTextField() {
        return textField;
    }
}
