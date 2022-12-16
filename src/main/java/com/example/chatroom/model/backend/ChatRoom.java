package com.example.chatroom.model.backend;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom implements Serializable {
    Map<String, User> userHashMap = Collections.synchronizedMap(new HashMap<>()) ;
    static int nextID;//赋予给新创建的群，然后nextID++;
    private int ID;
    private String chatroomName = null;

    public ChatRoom(int ID) {
        this.ID = ID;
        chatroomName = "新建聊天室" + ID;
    }

    public int getID() {
        return ID;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }

    public Map<String, User> getUserHashMap() {
        return userHashMap;
    }

    @Override
    public String toString() {
        return chatroomName;
    }
}
