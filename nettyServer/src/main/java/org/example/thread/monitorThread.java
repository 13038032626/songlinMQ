package org.example.thread;

import org.example.queue.MyQueue;

import java.util.Scanner;

public class monitorThread implements Runnable {
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("输入您的指令");
            String input = sc.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.exit(0);
            } else if ("show queue".equals(input)) {
                System.out.println("MyQueue.pushQueue = " + MyQueue.details());
            } else if (input.startsWith("add")) {
                String substring = input.substring(3);
                MyQueue.addPush(new String(substring.getBytes()));
            }
        }
    }
}
