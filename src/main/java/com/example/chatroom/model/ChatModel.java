package com.example.chatroom.model;

import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.ChatResponse;
import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.reponses.CreateChatroomResponse;

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

    public Optional<ChatObject> sendMessage(String message) {
        try {
            ChatResponse chatResponse = client.chat(message);
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
        client.createChatroom(user.getUserAccount());
        CreateChatroomResponse response = client.createChatroomResponse(user);
        ChatObject createChatroomObject = new ChatObject(response);
        chatObject = Optional.of(createChatroomObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
