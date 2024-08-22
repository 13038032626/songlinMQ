package org.example.handler.diskBased;

public class SegmentFile {
    String fileName;
    int index;
    static final int SEGMENT_LIMIT = 1024 * 1024;
    public SegmentFile(long fileName){
        this.fileName = fileName;
        index = 0;
    }
    public boolean isFull(){
        return index == SEGMENT_LIMIT;
    }
}
