package com.example.chatroom.model.backend;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User implements Serializable {
    private String userAccount;
    private String userPassWord;
    private String userName;
    private List<ChatRoom> chatRoomList = Collections.synchronizedList(new ArrayList<>());
    private List<User> friendsList = Collections.synchronizedList(new ArrayList<>());

    public User(String userAccount, String userPassWord, String userName, Socket userSocket) {
        this.userAccount = userAccount;
        this.userPassWord = userPassWord;
        this.userName = userName;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserPassWord() {
        return userPassWord;
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

    public List<User> getFriendsList() {
        return friendsList;
    }

    public String getUserName() {
        return userName;
    }
}
