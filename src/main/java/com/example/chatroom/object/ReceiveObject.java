package com.example.chatroom.object;

import com.example.chatroom.backend.entity.ChatRoom;
import com.example.chatroom.backend.entity.User;

/**
 * 从服务器被动接收的信息。
 */
public class ReceiveObject {
    private String sender;
    private int chatroomID;
    private String message;
    private ChatRoom chatRoom;
    private User user;

    public ReceiveObject(String sender, int chatroomID, String chatContents) {
        this.sender = sender;
        this.chatroomID = chatroomID;
        this.message = chatContents;
    }

    public ReceiveObject() {

    }

    public int getChatroomID() {
        return chatroomID;
    }

    public void setChatroomID(int chatroomID) {
        this.chatroomID = chatroomID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
