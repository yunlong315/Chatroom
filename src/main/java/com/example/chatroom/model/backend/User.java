package com.example.chatroom.model.backend;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 存储用户的各种属性的类
 */
public class User implements Serializable {
    private String userAccount;
    private String userPassWord;
    private String userName;
    private byte[] userImage;
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

    public void addFriend(User user) {
        friendsList.add(user);
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

    @Override
    public boolean equals(Object o) {
        return userAccount.equals(((User) o).getUserAccount());
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFriendsList(List<User> friendsList) {
        this.friendsList = friendsList;
    }

    public void setChatRoomList(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getImagePath() {
        return "src/main/java/data/userImage/defaultImg";
        //return String.format("data/userImage/%s",userAccount);
    }

}
