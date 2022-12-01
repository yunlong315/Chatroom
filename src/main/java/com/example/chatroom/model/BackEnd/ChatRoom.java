package com.example.chatroom.model.BackEnd;

import java.io.Serializable;
import java.util.HashMap;

public class ChatRoom implements Serializable
{
    HashMap<String,User>userHashMap=new HashMap<>();
    static int nextID;//赋予给新创建的群，然后nextID++;

    private int ID;
    public ChatRoom(int ID)
    {
        this.ID=ID;
    }



}
