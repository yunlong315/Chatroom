package com.example.chatroom;

import com.example.chatroom.model.backend.User;

public class Message {
    private String message;
    private String userAccount;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return CachedData.getUser(userAccount);
    }

    public Message(String message, String userAccount) {
        this.message = message;
        this.userAccount = userAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }
}
