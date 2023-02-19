package com.example.socializer;

import android.os.Message;

public class MessageModel {
    private String msgId,senderId,message;

    public MessageModel(){}

    public MessageModel(String msgId, String senderId, String message){
        this.msgId=msgId;
        this.senderId=senderId;
        this.message=message;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
