package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;

public class ReceiveObject {
    private String sender;
    private int chatroomID;
    private String message;
    private ChatRoom chatRoom;

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

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
