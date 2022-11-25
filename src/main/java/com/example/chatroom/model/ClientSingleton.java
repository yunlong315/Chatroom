package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.Client;

public class ClientSingleton {
    //每个客户端有唯一client
    private static Client client;
    public static Client getClient()
    {
        if(client == null)
        {
            client = new Client();
        }
        return client;
    }
}
