package com.example.chatroom.backend.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储聊天室的各种属性的类
 */
public class ChatRoom implements Serializable {
    /**
     * 赋予给新创建的群，然后nextID++
     */

    public static int nextID;
    private final int ID;
    private Map<String, User> userHashMap = Collections.synchronizedMap(new HashMap<>());
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

    public void setUserHashMap(Map<String, User> userHashMap) {
        this.userHashMap = userHashMap;
    }

    @Override
    public String toString() {
        return chatroomName;
    }

    @Override
    public boolean equals(Object obj) {
        return ID == ((ChatRoom) obj).getID();
    }
}
