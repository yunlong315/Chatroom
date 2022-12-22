package com.example.chatroom.model;

import com.example.chatroom.CachedData;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.model.backend.reponses.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


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

    // changeToChatList方法已删除

    public Optional<ChatObject> addFriend(String myAccount, String friendAccount) {
        AddFriendResponse response = client.addFriend(myAccount, friendAccount);
        ChatObject addFriendObject = new ChatObject(response);
        chatObject = Optional.of(addFriendObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_FriendsList);
        return chatObject;
    }

    /**
     * 发送聊天信息。
     *
     * @param message-聊天信息
     * @param chatroomID-聊天室id
     * @return chatObject
     */
    public Optional<ChatObject> sendMessage(String message, int chatroomID) {
        try {
            ChatResponse chatResponse = client.chat(message, user.getUserAccount(), chatroomID);
            ChatObject sendMessageObject = new ChatObject(chatResponse);
            chatObject = Optional.of(sendMessageObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CachedData.addMessage(user.getUserAccount(), chatroomID, message);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_SENDED);
        return chatObject;
    }

    /**
     * 由client发送创建聊天室的请求，获得response后发布。
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

    public void joinChatroom(int chatroomID) {
        JoinChatroomResponse response = client.joinChatroom(user.getUserAccount(), chatroomID);
        ChatObject joinChatroomObject = new ChatObject(response);
        chatObject = Optional.of(joinChatroomObject);
        //TODO:目前加入聊天室成功后通过EVENT_MODEL_UPDATE_ChatList更新，并没有使用EVENT_MODEL_JOIN_ChatRoom
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 此方法只在client的读线程被调用
     */
    /**
     *
     * @param sender-发送者账号
     * @param chatroomId
     * @param message
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
     * 更新chatroom
     *
     * @param chatroom-更新的chatroom
     */
    public void updateChatroom(ChatRoom chatroom) {
        chatroom = CachedData.getChatroom(chatroom.getID());
        receiveObject.setChatRoom(chatroom);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ONECHATROOM);
    }

    /**
     * 被其他user加为好友。在客户端加该user为好友。
     *
     * @param user-被该user加为好友
     */
    public void beAddedAsFriend(User user) {
        CachedData.addUser(user);
        user = CachedData.getUser(user.getUserAccount());
        chatObject = Optional.of(new ChatObject(user));
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_FriendsList);
    }

    public void changeRoomName(String newName, int roomId) {
        IResponse changeRoomNameResponse = client.changeChatroomName(roomId,newName);
        ChatObject changeRoomNameObject = new ChatObject(changeRoomNameResponse);
        chatObject = Optional.of(changeRoomNameObject);
        notifications.publish(Notifications.EVENT_MODEL_OPERATION_DONE);
    }

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
     * 更新一个user
     * @param user-有更新的user
     */
    public void updateUser(User user) {
        CachedData.addUser(user);
        //通知视图层进行更新
    }

    public void beInvitedToRoom(ChatRoom chatroom) {
        CachedData.addChatRoom(chatroom);
        chatroom = CachedData.getChatroom(chatroom.getID());
        ChatObject createChatroomObject = new ChatObject(chatroom);
        chatObject = Optional.of(createChatroomObject);
        notifications.publish(Notifications.EVENT_MODEL_UPDATE_ChatList);
    }

}
