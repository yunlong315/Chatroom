package com.example.chatroom.model.BackEnd.reponses;

import com.example.chatroom.model.BackEnd.ChatRoom;
import com.example.chatroom.model.BackEnd.User;

public class ChatResponse implements IResponse{
    private boolean success = false;
    private String errorMessage = "";
    private final User user;
    private final ChatRoom chatroom;
    //todo:需要包含所发送的消息
    private final String message="";

    //失败信息构造方法
    public ChatResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
        this.chatroom = null;
    }

    //成功信息构造方法
    public ChatResponse(User user, ChatRoom chatroom) {
        this.success = true;
        this.user = user;
        this.chatroom = chatroom;
        this.errorMessage = null;
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
