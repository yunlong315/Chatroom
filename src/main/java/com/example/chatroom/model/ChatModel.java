package com.example.chatroom.model;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.ChatResponse;
import com.example.chatroom.model.backend.reponses.CreateChatroomResponse;
import com.example.chatroom.model.backend.reponses.JoinChatroomResponse;

import java.util.Optional;

public class ChatModel {
    private Notifications notifications = new Notifications();
    private Optional<ChatObject> chatObject = Optional.empty();
    private final Client client;
    private User user;

    public ChatModel() {
        client = Client.getClient();
    }

    public Optional<ChatObject> getChatObject() {
        return chatObject;
    }

    // changeToChatList方法已删除

    public Optional<ChatObject> addFriend(String account) {
        return chatObject;
    }

    /**
     * 发送聊天信息。
     * @param message-聊天信息
     * @param chatroomID-聊天室id
     * @return
     */
    public Optional<ChatObject> sendMessage(String message, int chatroomID) {
        try {
            ChatResponse chatResponse =client.chat(message, user.getUserAccount(), chatroomID);
            ChatObject sendMessageObject = new ChatObject(chatResponse);
            chatObject = Optional.of(sendMessageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_MESSAGE);
        return chatObject;
    }
    /**
     * 由client发送创建聊天室的请求，获得response后发布。
     */
    public void createChatroom() {
        try {
            CreateChatroomResponse response=client.createChatroom(user);
            ChatObject createChatroomObject = new ChatObject(response);
            chatObject = Optional.of(createChatroomObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }
    public Optional<ChatObject> joinChatroom(int chatroomID) {
        JoinChatroomResponse response=client.joinChatroom(user.getUserAccount(), chatroomID);
        ChatObject joinChatroomObject = new ChatObject(response);
        chatObject = Optional.of(joinChatroomObject);
        notifications.publish(Notifications.EVENT_MODEL_JOIN_ChatRoom);
        return chatObject;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
