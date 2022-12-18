package com.example.chatroom;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;

import java.util.*;

public class CachedData {
    private static HashMap<Integer, ChatRoom> chatRoomHashMap = new HashMap<>();
    private static HashMap<Integer, List<Message>> messageListHashMap = new HashMap<>();
    private static HashMap<String,User> userHashMap = new HashMap<>();

    /**
     * 增加房间，若已有相应聊天室则更新聊天室信息。
     * @param chatRoom
     */
    public static void addChatRoom(ChatRoom chatRoom)
    {
        if(chatRoomHashMap.containsKey(chatRoom.getID()))
        {
            ChatRoom oldRoom = chatRoomHashMap.get(chatRoom.getID());
            oldRoom.setChatroomName(chatRoom.getChatroomName());
            oldRoom.setUserHashMap(chatRoom.getUserHashMap());
        }
        else
        {
            chatRoomHashMap.put(chatRoom.getID(),chatRoom);
        }
        System.out.println("update chatroom"+chatRoom.getID());
        for(User user:chatRoom.getUserHashMap().values())
        {
            System.out.println("update user"+user.getUserAccount());
            addUser(user);
        }

    }

    /**
     * 根据id得到chatroom.
     * @param id
     * @return
     */
    public static ChatRoom getChatroom(int id)
    {
        return chatRoomHashMap.get(id);
    }

    /**
     * 储存房间中的消息。
     * @param userAccount
     * @param chatroomId
     * @param message
     */
    public static void addMessage(String userAccount, int chatroomId, String message)
    {
        if(!messageListHashMap.containsKey(chatroomId))
        {
            List<Message> messageList = new ArrayList<>();
            messageListHashMap.put(chatroomId, messageList);
        }
        List<Message> messageList = messageListHashMap.get(chatroomId);
        messageList.add(new Message(message,userAccount));
    }
    public static User getUser(String userAccount)
    {
        return userHashMap.get(userAccount);
    }

    public static void addUser(User user)
    {
        if(userHashMap.containsKey(user.getUserAccount()))
        {
            //根据新user更新旧user
            User oldUser = userHashMap.get(user.getUserAccount());
            oldUser.setUserName(user.getUserName());
            oldUser.setFriendsList(user.getFriendsList());
            oldUser.setChatRoomList(user.getChatRoomList());
        }
        else
        {
            userHashMap.put(user.getUserAccount(),user);
        }
    }


    /**
     * 获取聊天室编号对应的消息列表。
     * @param chatroomId-聊天室编号
     * @return 聊天室编号对应的消息列表。
     */
    public static List<Message> getMessageList(int chatroomId)
    {
        if(!messageListHashMap.containsKey(chatroomId))
        {
            return new ArrayList<>();
        }
        return messageListHashMap.get(chatroomId);
    }
}

