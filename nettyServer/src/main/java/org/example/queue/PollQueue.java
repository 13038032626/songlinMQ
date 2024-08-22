package org.example.queue;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PollQueue extends QueueWithDetails{
    public PollQueue(BlockingQueue<String> queue, String description) {
        super(queue, description);
    }
    List<Channel> targetChannel = new ArrayList<>();


}
