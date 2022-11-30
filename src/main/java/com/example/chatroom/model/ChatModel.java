package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.ChatRoom;
import com.example.chatroom.model.BackEnd.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatModel {
    private Notifications notifications = new Notifications();
    private Optional<ChatObject> chatObject = Optional.empty();

    public ChatModel() {
        Client client = Client.getClient();
        client.init();
    }

    public Optional<ChatObject> getChatObject() {
        return chatObject;
    }

//    public List<ChatRoom> changeToChatList() {
//        //todo:从后端得到当前用户所在的所有聊天室的列表
//        //模拟从后端得到的聊天室列表
//        List<ChatRoom> chatRooms = new ArrayList<>();
//        chatRooms.add(new ChatRoom("1", "聊天室1"));
//        chatRooms.add(new ChatRoom("2", "聊天室2"));
//        chatRooms.add(new ChatRoom("3", "聊天室3"));
//        chatRooms.add(new ChatRoom("4", "聊天室4"));
//        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
//        return chatRooms;
//    }

    public Optional<ChatObject> addChatRoom() {


        return chatObject;
    }

    public Optional<ChatObject> addFriend(String account) {

        return chatObject;
    }

    public Optional<ChatObject> sendMessage(String message) {

        return chatObject;
    }
}
