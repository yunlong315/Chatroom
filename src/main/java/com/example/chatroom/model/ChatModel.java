package com.example.chatroom.model;

import com.example.chatroom.CachedData;
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
    /**
     * 储存读线程中的信息。
     */
    private ReceiveObject receiveObject = null;
    private final Client client;
    private User user;

    public ChatModel() {
        client = Client.getClient();
        client.setChatModel(this);
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
     *
     * @param message-聊天信息
     * @param chatroomID-聊天室id
     * @return chatObject
     */
    public Optional<ChatObject> sendMessage(String message, int chatroomID) {
        try {
            ChatResponse chatResponse = client.chat(message, user.getUserAccount(), chatroomID);
            ChatObject sendMessageObject = new ChatObject(chatResponse);
            chatObject = Optional.of(sendMessageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CachedData.addMessage(user.getUserAccount(), chatroomID, message);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_SENDED);
        return chatObject;
    }

    /**
     * 由client发送创建聊天室的请求，获得response后发布。
     */
    public void createChatroom() {
        try {
            CreateChatroomResponse response = client.createChatroom(user);
            ChatObject createChatroomObject = new ChatObject(response);
            chatObject = Optional.of(createChatroomObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    public void joinChatroom(int chatroomID) {
        JoinChatroomResponse response = client.joinChatroom(user.getUserAccount(), chatroomID);
        ChatObject joinChatroomObject = new ChatObject(response);
        chatObject = Optional.of(joinChatroomObject);
        //TODO:目前加入聊天室成功后通过EVENT_MODEL_UPDATE_ChatList更新，并没有使用EVENT_MODEL_JOIN_ChatRoom
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 此方法只在client的读线程被调用
     *
     * @param message
     */
    public void receiveMsg(String sender, int chatroomId, String message) {
        receiveObject = new ReceiveObject(sender, chatroomId, message);
        CachedData.addMessage(sender, chatroomId, message);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_MESSAGE);
    }

    public ReceiveObject getReceiveObject() {
        return receiveObject;
    }
}
