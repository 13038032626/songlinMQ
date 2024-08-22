package org.example.message.datas;

import com.google.gson.Gson;

public class TextMessage implements Data {

    String routerKey;

    String expiredTime;

    String priorityRank;

    String pushOrPoll;

    String delayedTime;

    String data;

    public TextMessage(String message) {
        this.data = message;
    }

    public TextMessage(String id, String message) {

    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
