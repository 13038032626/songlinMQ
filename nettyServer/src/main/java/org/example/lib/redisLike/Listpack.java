package org.example.lib.redisLike;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Listpack {
    private List<Entry> entries;

    public Listpack() {
        this.entries = new ArrayList<>();
    }

    // 添加消息
    public void addMessage(String message) {
        entries.add(new Entry(message));
    }

    // 获取消息
    public String getMessage(int index) {
        if (index < 0 || index >= entries.size()) {
            return null;
        }
        return entries.get(index).getValue();
    }
    public boolean isFull(){
        return entries.size() == 100;
    }

    // 删除消息（逻辑删除）
    public void removeMessage(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.get(index).markDeleted();
        }
    }

    // 压缩（物理删除已删除的消息）
    public void compress() {
        entries.removeIf(Entry::isDeleted);
    }

    // 获取所有有效的消息
    public List<String> getAllMessages() {
        List<String> result = new ArrayList<>();
        for (Entry entry : entries) {
            if (!entry.isDeleted()) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    // 序列化
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(estimateSerializedSize());
        for (Entry entry : entries) {
            entry.serialize(buffer);
        }
        return buffer.array();
    }

    // 反序列化
    public static Listpack deserialize(byte[] data) {
        Listpack listpack = new Listpack();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        while (buffer.hasRemaining()) {
            listpack.entries.add(Entry.deserialize(buffer));
        }
        return listpack;
    }

    // 估算序列化大小
    private int estimateSerializedSize() {
        int size = 0;
        for (Entry entry : entries) {
            size += entry.estimateSerializedSize();
        }
        return size;
    }

    // Listpack 内部 Entry 类
    private static class Entry {
        private String value;
        private boolean deleted;

        public Entry(String value) {
            this.value = value;
            this.deleted = false;
        }

        public String getValue() {
            return value;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void markDeleted() {
            this.deleted = true;
        }

        // 序列化单个 Entry
        public void serialize(ByteBuffer buffer) {
            byte[] valueBytes = value.getBytes();
            buffer.putInt(valueBytes.length);
            buffer.put(valueBytes);
            buffer.put((byte) (deleted ? 1 : 0));
        }

        // 反序列化单个 Entry
        public static Entry deserialize(ByteBuffer buffer) {
            int length = buffer.getInt();
            byte[] valueBytes = new byte[length];
            buffer.get(valueBytes);
            String value = new String(valueBytes);
            boolean deleted = buffer.get() == 1;
            Entry entry = new Entry(value);
            if (deleted) {
                entry.markDeleted();
            }
            return entry;
        }

        // 估算单个 Entry 的序列化大小
        public int estimateSerializedSize() {
            return Integer.BYTES + value.getBytes().length + 1;
        }
    }
}