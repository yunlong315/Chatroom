package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.ChatResponse;
import com.example.chatroom.model.backend.reponses.CreateChatroomResponse;
import com.example.chatroom.model.backend.reponses.IResponse;
import com.example.chatroom.model.backend.reponses.JoinChatroomResponse;

/**
 *
 */
public class ChatObject {
    private final boolean wasError;
    private final String errorMessage;
    private User sender = null;
    private String message = null;
    private ChatRoom chatRoom = null;

    public ChatObject(IResponse response) {
        this.wasError = !response.isSuccess();
        this.errorMessage = response.getErrorMessage();
        //根据具体的Response类进行构造
        if (response instanceof CreateChatroomResponse createChatroomResponse) {
            chatRoom = createChatroomResponse.getChatroom();
        } else if (response instanceof JoinChatroomResponse joinChatroomResponse) {
            chatRoom = joinChatroomResponse.getChatroom();
        } else if (response instanceof ChatResponse chatResponse) {
            message = chatResponse.getMessage();
            sender = chatResponse.getUser();
        }
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

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
