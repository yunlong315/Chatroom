package com.example.chatroom.backend.entity;

/**
 * 聊天消息类。
 */
public class Message {
    private final String message;
    private final String userAccount;

    public Message(String message, String userAccount) {
        this.message = message;
        this.userAccount = userAccount;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return CachedData.getUser(userAccount);
    }

    public String getUserAccount() {
        return userAccount;
    }
}
