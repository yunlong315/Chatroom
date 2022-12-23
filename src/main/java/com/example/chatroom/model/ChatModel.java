package com.example.chatroom.model;

import com.example.chatroom.CachedData;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.*;

import java.io.IOException;
import java.util.Optional;

/**
 * 聊天界面的model层。
 */
public class ChatModel {
    private Notifications notifications = new Notifications();
    private Optional<ChatObject> chatObject = Optional.empty();
    /**
     * 储存读线程中的信息。
     */
    private ReceiveObject receiveObject = new ReceiveObject();
    private final Client client;
    private User user;

    public ChatModel() {
        client = Client.getClient();
        client.setChatModel(this);
    }

    public Optional<ChatObject> getChatObject() {
        return chatObject;
    }

    /**
     * 添加好友。
     * @param myAccount-当前用户账号
     * @param friendAccount-要加为好友的用户账号
     */
    public void addFriend(String myAccount, String friendAccount) {
        AddFriendResponse response = client.addFriend(myAccount, friendAccount);
        ChatObject addFriendObject = new ChatObject(response);
        chatObject = Optional.of(addFriendObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_FriendsList);
    }

    /**
     * 发送聊天信息。
     *
     * @param message-聊天信息
     * @param chatroomID-聊天室id
     */
    public void sendMessage(String message, int chatroomID) {
        try {
            ChatResponse chatResponse = client.chat(message, user.getUserAccount(), chatroomID);
            ChatObject sendMessageObject = new ChatObject(chatResponse);
            chatObject = Optional.of(sendMessageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CachedData.addMessage(user.getUserAccount(), chatroomID, message);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_SENDED);
    }

    /**
     * 创建一个新聊天室。
     */
    public void createChatroom() {
        try {
            CreateChatroomResponse response = client.createChatroom(user);
            ChatObject createChatroomObject = new ChatObject(response);
            chatObject = Optional.of(createChatroomObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    /**
     * 加入聊天室。
     * @param chatroomID-房间号
     */
    public void joinChatroom(int chatroomID) {
        JoinChatroomResponse response = client.joinChatroom(user.getUserAccount(), chatroomID);
        ChatObject joinChatroomObject = new ChatObject(response);
        chatObject = Optional.of(joinChatroomObject);
        ChatRoom chatRoom = response.getChatroom();
        if(chatRoom == null)
        {
            System.out.println("服务器返回的chatroom为空");
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     *收到一条消息。此方法只在client的读线程被调用。
     * @param sender-发送者账号
     * @param chatroomId-消息所在房间号
     * @param message-消息内容
     */
    public void receiveMsg(String sender, int chatroomId, String message) {
        receiveObject.setMessage(message);
        receiveObject.setSender(sender);
        receiveObject.setChatroomID(chatroomId);
        CachedData.addMessage(sender, chatroomId, message);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_MESSAGE);
    }


    public ReceiveObject getReceiveObject() {
        return receiveObject;
    }







    /**
     * 更新chatroom。
     * @param chatroom-更新的chatroom
     */
    public void updateChatroom(ChatRoom chatroom) {
        CachedData.addChatRoom(chatroom);
        chatroom = CachedData.getChatroom(chatroom.getID());
        receiveObject.setChatRoom(chatroom);
        System.out.printf("model层接收到%d号聊天室更新\n",chatroom.getID());
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ONE_CHATROOM);
    }

    /**
     * 被其他user加为好友。该方法只在读线程被调用。。
     * @param user-被该user加为好友
     */
    public void beAddedAsFriend(User user) {
        CachedData.addUser(user);
        user = CachedData.getUser(user.getUserAccount());
        chatObject = Optional.of(new ChatObject(user));
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_FriendsList);
    }

    /**
     * 修改房间名。
     * @param newName-新的房间名
     * @param roomId-房间号
     */
    public void changeRoomName(String newName, int roomId) {
        IResponse changeRoomNameResponse = client.changeChatroomName(roomId,newName);
        ChatObject changeRoomNameObject = new ChatObject(changeRoomNameResponse);
        chatObject = Optional.of(changeRoomNameObject);
        notifications.publish(Notifications.EVENT_MODEL_OPERATION_DONE);
    }

    /**
     * 邀请好友进入房间。
     * @param userAccount-当前用户账号
     * @param friendAccount-好友账号
     * @param roomId-房间号
     */
    public void inviteFriend(String userAccount, String friendAccount, int roomId) {
        IResponse inviteFriendResponse = client.inviteFriend(userAccount, friendAccount, roomId);
        ChatObject inviteFriendObject = new ChatObject(inviteFriendResponse);
        chatObject = Optional.of(inviteFriendObject);
        if(chatObject.get().getChatRoom() == null)
        {
            System.out.println("拿到了一个空的chatroom");
            System.out.println(inviteFriendResponse.getErrorMessage());
        }
        updateChatroom(chatObject.get().getChatRoom());
    }

    /**
     * 更新一个user。
     * @param user-有更新的user
     */
    public void updateUser(User user) {
        CachedData.addUser(user);
        receiveObject.setUser(user);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ONE_USER);
    }

    /**
     * 当前用户被邀请入聊天室。此方法只在读线程被调用。
     * @param chatroom-被邀请入的聊天室
     */
    public void beInvitedToRoom(ChatRoom chatroom) {
        CachedData.addChatRoom(chatroom);
        chatroom = CachedData.getChatroom(chatroom.getID());
        ChatObject createChatroomObject = new ChatObject(chatroom);
        chatObject = Optional.of(createChatroomObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    /**
     * 更改用户头像。
     * @param userAccount-用户账号
     * @param filePath-头像路径
     * @throws IOException-如果发生IO错误。
     */
    public void changeUserHead(String userAccount, String filePath) throws IOException {
        client.setImage(userAccount, filePath);
    }
}
