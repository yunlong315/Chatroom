package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.ChatResponse;
import com.example.chatroom.model.BackEnd.User;

public class ChatObject {
    private boolean wasError;
    private String errorMessage;
    private User sender = null;
    private String message = "";
    public ChatObject(ChatResponse chatResponse) {
        this.wasError = !chatResponse.isSuccess();
        this.errorMessage = chatResponse.getErrorMessage();
        this.sender = chatResponse.getUser();
        this.message= chatResponse.getMessage();
    }

    public boolean isWasError() {
        return wasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public User getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
