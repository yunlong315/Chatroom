package com.example.chatroom.model.BackEnd;

public class ChatResponse {
    private boolean success = false;
    private String errorMessage = "";
    private final User user;
    private final ChatRoom chatrooms;
    //todo:需要包含所发送的消息
    private final String message="";

    //失败信息构造方法
    public ChatResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
        this.chatrooms = null;
    }

    //成功信息构造方法
    ChatResponse(User user, ChatRoom chatrooms) {
        this.success = true;
        this.user = user;
        this.chatrooms = chatrooms;
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
