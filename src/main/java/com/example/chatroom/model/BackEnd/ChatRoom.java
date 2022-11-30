package com.example.chatroom.model.BackEnd;

import java.util.HashMap;

public class ChatRoom {
    private String id;
    private String roomName;
    private HashMap<String, User> memberMap = new HashMap<>();

    public ChatRoom(String id, String roomName) {
        this.id = id;
        this.roomName = roomName;
    }

    public void addMember(User newUser) {
        memberMap.put(newUser.getUserAccount(), newUser);
    }

    public void removeMember(String userAccount) {
        memberMap.remove(userAccount);
    }

    public User queryMember(String userAccount) {
        return memberMap.get(userAccount);
    }

    public boolean contains(User user) {
        return memberMap.containsKey(user.getUserAccount());
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id='" + id + '\'' +
                ", roomName='" + roomName +
                '}';
    }
}
