package com.example.chatroom.model.BackEnd;

import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private String userAccount;
    private String userPassWord;
    private String userName;
    private transient Socket userSocket = null;

    public User(String userAccount, String userPassWord, String userName, Socket userSocket) {
        this.userAccount = userAccount;
        this.userName = userName;
        this.userPassWord = userPassWord;
        this.userSocket = userSocket;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserPassWord() {
        return userPassWord;
    }

    public void setUserSocket(Socket userSocket) {
        this.userSocket = userSocket;
    }

    public String getUserAccount() {
        return userAccount;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
