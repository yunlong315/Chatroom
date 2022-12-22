package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;

public class ReceiveObject {
    private String sender;
    private int chatroomID;
    private String message;
    private ChatRoom chatRoom;
    private User user;

    ReceiveObject(String sender, int chatroomID, String chatContents) {
        this.sender = sender;
        this.chatroomID = chatroomID;
        this.message = chatContents;
    }

    ReceiveObject() {

    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setChatroomID(int chatroomID) {
        this.chatroomID = chatroomID;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public User getUser() {
        return user;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
