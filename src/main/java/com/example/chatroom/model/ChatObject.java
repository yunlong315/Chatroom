package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.*;

/**
 * 主动请求后收到的返回存于此处
 */
public class ChatObject {
    private boolean wasError;
    /**
     * 报错消息
     */
    private String errorMessage;
    private ChatRoom chatRoom = null;
    private User user;

    public ChatObject(IResponse response) {
        this.wasError = !response.isSuccess();
        this.errorMessage = response.getErrorMessage();
        //根据具体的Response类进行构造
        if (response instanceof CreateChatroomResponse createChatroomResponse) {
            chatRoom = createChatroomResponse.getChatroom();
        } else if (response instanceof JoinChatroomResponse joinChatroomResponse) {
            chatRoom = joinChatroomResponse.getChatroom();
        } else if (response instanceof ChatResponse chatResponse) {
            //chatResponse只包含成功与否的信息
        } else if (response instanceof AddFriendResponse addFriendResponse) {
            user = addFriendResponse.getUser();
        }
    }
    public ChatObject(User user)
    {
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public boolean wasError() {
        return wasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
