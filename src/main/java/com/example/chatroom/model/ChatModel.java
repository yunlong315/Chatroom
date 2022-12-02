package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.reponses.ChatResponse;
import com.example.chatroom.model.BackEnd.Client;
import com.example.chatroom.model.BackEnd.reponses.CreateChatroomResponse;

import java.util.Optional;

public class ChatModel {
    private Notifications notifications = new Notifications();
    private Optional<ChatObject> chatObject = Optional.empty();
    private final Client client;

    public ChatModel() {
        client = Client.getClient();
        client.init();
    }

    public Optional<ChatObject> getChatObject() {
        return chatObject;
    }

//    public List<ChatRoom> changeToChatList() {
//        //todo:删除此方法，用户加入的聊天室列表将会在User类中包含
//        //模拟从后端得到的聊天室列表
//        List<ChatRoom> chatRooms = new ArrayList<>();
//        chatRooms.add(new ChatRoom("1", "聊天室1"));
//        chatRooms.add(new ChatRoom("2", "聊天室2"));
//        chatRooms.add(new ChatRoom("3", "聊天室3"));
//        chatRooms.add(new ChatRoom("4", "聊天室4"));
//        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
//        return chatRooms;
//    }

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
        CreateChatroomResponse response =client.createChatroom();
        ChatObject createChatroomObject = new ChatObject(response);
        chatObject = Optional.of(createChatroomObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }
}
