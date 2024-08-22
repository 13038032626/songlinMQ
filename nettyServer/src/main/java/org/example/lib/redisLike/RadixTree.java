package org.example.lib.redisLike;

import java.util.HashMap;
import java.util.Map;

public class RadixTree {
    private RadixTreeNode root = new RadixTreeNode();

    public void add(String id,String message){
        root.add(id,message);
    }
    public String get(String id){
        return  root.get(id);
    }
    public void remove(String id){
        root.remove(id);
    }
}
class RadixTreeNode {
    private Map<String,RadixTreeNode> children = new HashMap<>();
    private String message;
    public void add(String id,String message){
        if(id.isEmpty()){
            this.message = message;
        }
        String prefix = id.substring(0,1);
        RadixTreeNode child = children.computeIfAbsent(prefix, k -> new RadixTreeNode());
        child.add(id.substring(1),message);
    }
    public String get(String id) {
        if (id.isEmpty()) {
            return message;
        } else {
            String prefix = id.substring(0, 1);
            RadixTreeNode child = children.get(prefix);
            return child != null ? child.get(id.substring(1)) : null;
        }
    }
    public void remove(String id) {
        if (id.isEmpty()) {
            this.message = null;
        } else {
            String prefix = id.substring(0, 1);
            RadixTreeNode child = children.get(prefix);
            if (child != null) {
                child.remove(id.substring(1));
            }
        }
    }
    public static String generateID(){
        return String.valueOf(System.currentTimeMillis());
    }
}
