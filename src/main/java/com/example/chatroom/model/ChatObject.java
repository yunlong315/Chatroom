package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.CreateChatroomResponse;
import com.example.chatroom.model.backend.reponses.IResponse;
import com.example.chatroom.model.backend.reponses.JoinChatroomResponse;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class ChatObject {
    private final boolean wasError;
    private final String errorMessage;
    private final User sender = null;
    private final String message = "";
    private final List<ChatRoom> chatRoomList;
    public ChatObject(IResponse response) {
        this.wasError = !response.isSuccess();
        this.errorMessage = response.getErrorMessage();
        chatRoomList = new ArrayList<>();
        //根据具体的Response类进行构造
        if(response instanceof CreateChatroomResponse createChatroomResponse)
        {
            chatRoomList.add(createChatroomResponse.getChatroom());
        }
        else if (response instanceof JoinChatroomResponse joinChatroomResponse)
        {
            chatRoomList.add(joinChatroomResponse.getChatroom());
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

    public List<ChatRoom> getChatRoomList() {
        return chatRoomList;
    }
}
