package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;

public class ChatResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private final User user;
    private final ChatRoom chatroom;
    //todo:需要包含所发送的消息
    private String message;

    //失败信息构造方法
    public ChatResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
        this.chatroom = null;
    }

    //成功信息构造方法
    public ChatResponse(User user, ChatRoom chatroom, String message) {
        this.success = true;
        this.user = user;
        this.chatroom = chatroom;
        this.errorMessage = null;
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
