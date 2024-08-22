package org.example.message;

public class ManageMessage extends BaseMessage{

    Integer manageType; // 0表示创建poll，1表示创建push

    public ManageMessage(Integer manageType){
        this.messageType = 0;
        this.manageType = manageType;
    }


}
