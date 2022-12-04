package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.ChatRoom;

public class CreateChatroomResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private final ChatRoom chatroom;

    //失败信息构造方法
    public CreateChatroomResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.chatroom = null;
    }

    //成功信息构造方法
    public CreateChatroomResponse(ChatRoom chatroom) {
        this.success = true;
        this.chatroom = chatroom;
        this.errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public ChatRoom getChatroom() {
        return chatroom;
    }
}
