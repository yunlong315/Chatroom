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
    /**
     * 报错消息
     */
    private final String errorMessage;
    /**
     * 收到的聊天消息
     */
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
            //TODO:此处不需要sender
            //sender = chatResponse.getUser();
        }
    }

    public boolean wasError() {
        return wasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public String getMessage() {
        return message;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
