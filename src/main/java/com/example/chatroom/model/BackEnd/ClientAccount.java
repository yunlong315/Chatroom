package com.example.chatroom.model.BackEnd;

import java.net.Socket;

public class ClientAccount {
    String id;
    String pwd;
    Socket clientSocket = null;

    public ClientAccount(String id, String pwd, Socket clientSocket) {
        this.id = id;
        this.pwd = pwd;
        this.clientSocket = clientSocket;
    }
}
