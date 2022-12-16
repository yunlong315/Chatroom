package com.example.chatroom.model;

public class ReceiveObject {
    private final String sender;
    private final int chatroomID;
    private final String message;

    ReceiveObject(String sender, int chatroomID, String chatContents) {
        this.sender = sender;
        this.chatroomID = chatroomID;
        this.message = chatContents;
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
