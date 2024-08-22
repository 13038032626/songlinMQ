package org.example.message.datas;

public class TypeConverter {
    public static Data convert(String message){
        return new TextMessage(message);
    }
}
