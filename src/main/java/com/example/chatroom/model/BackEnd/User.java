package com.example.chatroom.model.BackEnd;


import java.net.Socket;

public class User {
    private String userAccount;
    private String userPassWord;
    private String userName;
    private Socket userSocket = null;
    User(String userAccount,String userPassWord,String userName,Socket userSocket)
    {
        this.userAccount = userAccount;
        this.userName = userName;
        this.userPassWord = userPassWord;
        this.userSocket = userSocket;
    }

}
