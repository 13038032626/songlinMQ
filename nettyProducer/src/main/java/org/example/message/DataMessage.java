package org.example.message;

import org.example.message.datas.data;

import java.util.HashMap;
import java.util.List;
public class DataMessage extends BaseMessage{

    public data data;

    List<Integer> targetQueue;

    HashMap<String,Integer> details;

}
