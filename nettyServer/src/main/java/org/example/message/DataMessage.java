package org.example.message;

import org.example.message.datas.Data;

import java.util.HashMap;
import java.util.List;

public class DataMessage extends BaseMessage{

    public Data data;

    List<Integer> targetQueue;

    HashMap<String,Integer> details;
}
