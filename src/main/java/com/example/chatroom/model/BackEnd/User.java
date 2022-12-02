package com.example.chatroom.model.BackEnd;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userAccount;
    private String userPassWord;
    private String userName;
    private transient Socket userSocket = null;
    //TODO:chatRoomList储存用户目前加入的所有聊天室
    private List<ChatRoom> chatRoomList = new ArrayList<>();
    //TODO:(暂不考虑)friendsList储存用户目前所有好友
    private List<User> userList = new ArrayList<>();

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

    public List<ChatRoom> getChatRoomList() {
        return chatRoomList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public String getUserName() {
        return userName;
    }
}
