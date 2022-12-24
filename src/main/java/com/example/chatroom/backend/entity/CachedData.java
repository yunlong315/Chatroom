package com.example.chatroom.backend.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 储存前端数据。
 */
public class CachedData {
    private static final Hashtable<Integer, ChatRoom> chatRoomHashMap = new Hashtable<>();
    private static final Hashtable<Integer, List<Message>> messageListHashMap = new Hashtable<>();
    private static final Hashtable<String, User> userHashMap = new Hashtable<>();

    /**
     * 增加房间，若已有相应聊天室则更新聊天室信息。
     *
     * @param chatRoom-要更新的聊天室
     */
    public static void addChatRoom(ChatRoom chatRoom) {
        if (chatRoomHashMap.containsKey(chatRoom.getID())) {
            ChatRoom oldRoom = chatRoomHashMap.get(chatRoom.getID());
            oldRoom.setChatroomName(chatRoom.getChatroomName());
            oldRoom.setUserHashMap(chatRoom.getUserHashMap());
        } else {
            chatRoomHashMap.put(chatRoom.getID(), chatRoom);
        }
        for (User user : chatRoom.getUserHashMap().values()) {
            addUser(user);
        }
    }

    /**
     * 根据房间号得到聊天室.
     *
     * @param id-房间号
     * @return
     */
    public static ChatRoom getChatroom(int id) {
        return chatRoomHashMap.get(id);
    }

    /**
     * 储存房间中的消息。
     *
     * @param userAccount-用户账号
     * @param chatroomId-房间号
     * @param message-消息内容
     */
    public static void addMessage(String userAccount, int chatroomId, String message) {
        if (!messageListHashMap.containsKey(chatroomId)) {
            List<Message> messageList = new ArrayList<>();
            messageListHashMap.put(chatroomId, messageList);
        }
        List<Message> messageList = messageListHashMap.get(chatroomId);
        messageList.add(new Message(message, userAccount));
    }

    public static User getUser(String userAccount) {
        return userHashMap.get(userAccount);
    }

    /**
     * 更新用户信息。
     *
     * @param user-目标用户
     */
    public static void addUser(User user) {
        if (userHashMap.containsKey(user.getUserAccount())) {
            //根据新user更新旧user
            User oldUser = userHashMap.get(user.getUserAccount());
            oldUser.setUserName(user.getUserName());
            oldUser.setFriendsList(user.getFriendsList());
            oldUser.setChatRoomList(user.getChatRoomList());
            oldUser.setUserImage(user.getUserImage());
            saveImage(oldUser);
        } else {
            userHashMap.put(user.getUserAccount(), user);
        }
    }

    /**
     * 在本地存放用户头像。
     *
     * @param user-目标用户
     */
    private static void saveImage(User user) {
        byte[] image = user.getUserImage();
        if (user.isHasImage() && image != null) {
            //更新头像
            String path = String.format("src/main/java/data/userImage/%s", user.getUserAccount());
            File trg = new File(path);
            if (!trg.exists()) {
                trg.getParentFile().mkdirs();
                try {
                    trg.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                OutputStream os = new FileOutputStream(trg);
                os.write(image, 0, image.length);
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("%s的头像已存于本地\n", user.getUserName());
        }
    }


    /**
     * 获取聊天室编号对应的消息列表。
     *
     * @param chatroomId-聊天室编号
     * @return 聊天室编号对应的消息列表。聊天室没有消息时返回空列表。
     */
    public static List<Message> getMessageList(int chatroomId) {
        if (!messageListHashMap.containsKey(chatroomId)) {
            return new ArrayList<>();
        }
        return messageListHashMap.get(chatroomId);
    }
}

