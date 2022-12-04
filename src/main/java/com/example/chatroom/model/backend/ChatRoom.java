package com.example.chatroom.model.backend;

import java.io.Serializable;
import java.util.HashMap;

public class ChatRoom implements Serializable {
    HashMap<String, User> userHashMap = new HashMap<>();
    static int nextID;//赋予给新创建的群，然后nextID++;
    private int ID;
    private String chatroomName = "新建聊天室";
    public ChatRoom(int ID) {
        this.ID = ID;
    }
}
