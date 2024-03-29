package com.example.chatroom.backend.socket;

import com.example.chatroom.backend.entity.ChatRoom;
import com.example.chatroom.backend.entity.User;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Map;

/**
 * 服务端每监听到一个客户端连接，就新建一个线程
 */
public class ClientThread extends Thread {
    CenterServer cs;
    String userAccount = null;
    private final ClientIO io;

    public ClientThread(Socket socket) {
        cs = CenterServer.getCenterServer();
        io = new ClientIO(socket);
    }

    /**
     * 线程启动方法，不断获取Client发送的数据
     */
    @Override
    public void run() {
        try {
            while (true) {
                // 获取Client发送的数据，格式为”数据长度+数据“
                int len = io.dataInputStream.readInt();
                byte[] data = new byte[len - 5];
                io.dataInputStream.readFully(data);
                String[] cmd = new String(data).split("/");
                switch (cmd[0]) {
                    case "register":
                        // cmd = ["register", userAccount, pwd, userName]
                        register(cmd);
                        break;
                    case "login":
                        // cmd = ["login", userAccount, pwd]
                        login(cmd);
                        break;
                    case "createChatroom":
                        // cmd = ["createChatroom", userAccount]
                        createChatroom(cmd);
                        break;
                    case "joinChatroom":
                        // cmd = ["joinChatroom", userAccount,chatroomID]
                        joinChatroom(cmd);
                        break;
                    case "chat":
                        // cmd = ["chat", userAccount,chatroomID,chatcontents]
                        chat(new String(data));
                        break;
                    case "addFriend":
                        // cmd = ["addFriend", userAccount, friendAccount]
                        addFriend(cmd);
                        break;
                    case "inviteFriend":
                        // cmd = ["inviteFriend", userAccount, friendAccount, chatroomID]
                        inviteFriend(cmd);
                        break;
                    case "setImage":
                        // cmd = ["setImage", userAccount, byte[] img]
                        setImage(data);
                        break;
                    case "changeChatroomName":
                        // cmd = ["changeChatroomName", chatroomID, chatroomName]
                        changeChatroomName(cmd);
                        break;
                    default:
                        System.out.println(cmd[0] + " not found");
                        break;
                }
            }
        } catch (SocketException | EOFException e) {
            logout();
            System.out.println("User logout");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("User exit unexpectedly");
        }
    }

    /**
     * 用户注册
     *
     * @param cmd cmd = ["register", userAccount, pwd, userName]
     */
    private void register(String[] cmd) {
        // cmd = ["register", userAccount, pwd, userName]
        Map<String, User> clientMap = cs.clientMap;
        String userAccount = cmd[1];
        if (clientMap.containsKey(userAccount)) {
            System.out.println("账号重复");
            io.sendMsg("registerResponse/账号重复");
        } else {
            clientMap.put(userAccount, new User(userAccount, cmd[2], cmd[3], io.socket));
            System.out.println("注册成功! 账号为: " + userAccount + "; 目前已注册账号有：");
            for (Map.Entry<String, User> entry : clientMap.entrySet()) {
                System.out.println("\tuserAccount = " + entry.getKey() + ", userName = " + entry.getValue().getUserName());
            }
            io.sendMsg("registerResponse/success");
        }
    }

    /**
     * 用户登录
     *
     * @param cmd cmd = ["login", userAccount, pwd]
     */
    private void login(String[] cmd) {
        // cmd = ["login", userAccount, pwd]
        Map<String, User> clientMap = cs.clientMap;
        if (!clientMap.containsKey(cmd[1])) {
            // 账号不存在
            System.out.println("账号不存在");
            io.sendMsg("loginResponse/账号不存在");
        } else if (!clientMap.get(cmd[1]).getUserPassWord().equals(cmd[2])) {
            // 账号密码错误
            System.out.println("账号密码错误");
            io.sendMsg("loginResponse/账号密码错误");
        } else {
            // 登录成功
            io.sendObject("loginResponse/success/", clientMap.get(cmd[1]));
            userAccount = cmd[1];
            cs.clientIOMap.put(userAccount, io);

            // io.sendMsg("loginResponse/success/" + cmd[1] + "/" + cmd[2] + "/" + clientMap.get(cmd[1]).getUserName());
            System.out.println("登录成功! 账号为: " + cmd[1]);
        }
    }

    /**
     * 用户创建聊天室
     *
     * @param cmd cmd = ["createChatroom", userAccount]
     */
    private void createChatroom(String[] cmd) {
        // cmd = ["createChatroom", userAccount]
        int chatroomID = ChatRoom.nextID++;
        String userAccount = cmd[1];
        ChatRoom newChatroom = new ChatRoom(chatroomID);
        Map<String, User> clientMap = cs.clientMap;
        User thisUser = clientMap.get(userAccount);
        newChatroom.getUserHashMap().put(userAccount, thisUser);
        thisUser.getChatRoomList().add(newChatroom);
        cs.chatroomHashMap.put(newChatroom.getID(), newChatroom);
        System.out.println("创建聊天室成功，聊天室ID为：" + chatroomID);
        io.sendMsg("createChatroomResponse/success/" + chatroomID);
    }

    /**
     * 将用户加入聊天室，并向所有聊天室中用户广播这个更新。
     *
     * @param cmd cmd = ["addChatroom", userAccount, chatroomID]
     */
    private void joinChatroom(String[] cmd) {
        // cmd = ["addChatroom", userAccount, chatroomID]
        String userAccount = cmd[1];
        int chatroomID = Integer.parseInt(cmd[2]);
        Map<Integer, ChatRoom> chatroomHashMap = cs.chatroomHashMap;
        // 判断聊天室是否存在
        if (!chatroomHashMap.containsKey(chatroomID)) {
            io.sendMsg("joinChatroomResponse/聊天室不存在");
            System.out.println(chatroomID + "号聊天室不存在");
            return;
        }
        User thisUser = cs.clientMap.get(userAccount);
        ChatRoom thisChatroom = chatroomHashMap.get(chatroomID);
        // 判断用户是否已经在聊天室中
        if (thisChatroom.getUserHashMap().containsKey(userAccount)) {
            io.sendMsg("joinChatroomResponse/用户已在聊天室中");
            System.out.println(userAccount + "用户已在" + chatroomID + "号聊天室中");
            return;
        }
        // 成功加入
        thisChatroom.getUserHashMap().put(userAccount, thisUser);
        thisUser.getChatRoomList().add(thisChatroom);
        io.sendObject("joinChatroomResponse/success/", thisChatroom);
        System.out.printf("%s用户成功加入%d号聊天室\n", userAccount, chatroomID);
        // 向该聊天室中其他成员广播新用户加入的信息
        Map<String, ClientIO> clientIOHashMap = cs.clientIOMap;
        for (User user : thisChatroom.getUserHashMap().values()) {
            if (clientIOHashMap.containsKey(user.getUserAccount())) {
                ClientIO io = cs.clientIOMap.get(user.getUserAccount());
                io.sendObject("joinChatroomRequest/", thisChatroom);
                io.sendMsg(String.format("receiveChatMsg/%s/%d/%s", userAccount, chatroomID, String.format("我是%s，一起聊聊吧！", thisUser.getUserName())));
            }
        }
    }

    /**
     * 用户发送聊天信息
     *
     * @param cmdStr cmd = ["chat", userAccount, chatroomID, chatContents]
     */
    private void chat(String cmdStr) {
        // cmd = ["chat", userAccount, chatroomID, chatContents]
        String[] cmd = cmdStr.split("/", 4);
        String chatContents = cmd[3];
        String userAccount = cmd[1];
        int chatroomID = Integer.parseInt(cmd[2]);
        //获取聊天室
        Map<Integer, ChatRoom> chatroomHashMap = cs.chatroomHashMap;
        ChatRoom thisChatroom = chatroomHashMap.get(chatroomID);
        //获取所有成员（包括自己）
        Map<String, User> userHashMap = thisChatroom.getUserHashMap();
        Map<String, ClientIO> clientIOHashMap = cs.clientIOMap;
        //对除自己外其他成员发送消息
        for (User user : userHashMap.values()) {
            if (clientIOHashMap.containsKey(user.getUserAccount()) && !user.getUserAccount().equals(userAccount)) {
                ClientIO io = cs.clientIOMap.get(user.getUserAccount());
                io.sendMsg(String.format("receiveChatMsg/%s/%d/%s", userAccount, chatroomID, chatContents));
            }
        }
        //向自己返回成功消息
        io.sendMsg("chatResponse/success");
        System.out.println("用户成功发送消息");
    }

    /**
     * 用户登出
     */
    private void logout() {
        if (userAccount != null) {
            // 删除clientIOMap中账号对应IO
            cs.clientIOMap.remove(userAccount);
        }
    }

    /**
     * 用户添加好友
     *
     * @param cmd cmd = ["addFriend", userAccount, friendAccount]
     */
    private void addFriend(String[] cmd) {
        // cmd = ["addFriend", userAccount, friendAccount]
        String userAccount = cmd[1];
        String friendAccount = cmd[2];
        Map<String, User> userHashMap = cs.clientMap;
        // 判断friendAccount是否已注册
        if (!userHashMap.containsKey(friendAccount)) {
            io.sendMsg("addFriendResponse/该账号尚未注册");
            System.out.println(friendAccount + "尚未注册");
            return;
        }
        // 成功添加好友
        User self = userHashMap.get(userAccount);
        User friend = userHashMap.get(friendAccount);
        self.getFriendsList().add(friend);
        friend.getFriendsList().add(self);
        // 返回好友对应User类
        io.sendObject("addFriendResponse/success/", friend);
        System.out.printf("%s成功添加%s为好友\n", userAccount, friendAccount);
        // 通知friend被添加好友
        ClientIO friendIO = cs.clientIOMap.get(friendAccount);
        friendIO.sendObject("addFriendRequest/success/", self);
    }

    /**
     * 用户邀请好友进入聊天室
     *
     * @param cmd cmd = ["inviteFriend", userAccount, friendAccount, chatroomID]
     */
    private void inviteFriend(String[] cmd) {
        // cmd = ["inviteFriend", userAccount, friendAccount, chatroomID]
        String userAccount = cmd[1];
        String friendAccount = cmd[2];
        int chatroomID = Integer.parseInt(cmd[3]);
        // 判断是否是好友
        Map<String, User> userHashMap = cs.clientMap;
        User self = userHashMap.get(userAccount);
        boolean flag = false;
        for (User fri : self.getFriendsList()) {
            if (fri.getUserAccount().equals(friendAccount)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            io.sendMsg("inviteFriendResponse/尚未成为好友");
            System.out.println(friendAccount + "非" + userAccount + "好友");
            return;
        }
        // 判断聊天室是否存在
        if (!cs.chatroomHashMap.containsKey(chatroomID)) {
            io.sendMsg("inviteFriendResponse/聊天室不存在");
            System.out.println(chatroomID + "号聊天室不存在");
            return;
        }
        // 判断好友是否已在聊天室中
        ChatRoom thisChatroom = cs.chatroomHashMap.get(chatroomID);
        if (thisChatroom.getUserHashMap().containsKey(friendAccount)) {
            io.sendMsg("inviteFriendResponse/好友已在聊天室中");
            System.out.println(friendAccount + "用户已在" + chatroomID + "号聊天室中");
            return;
        }
        // 成功加入，并通知好友
        User friend = cs.clientMap.get(friendAccount);
        thisChatroom.getUserHashMap().put(friendAccount, friend);
        friend.getChatRoomList().add(thisChatroom);

        String friendName = cs.clientMap.get(friendAccount).getUserName();
        io.sendObject("inviteFriendResponse/success/", thisChatroom);
        ClientIO friendIO = cs.clientIOMap.get(friendAccount);
        friendIO.sendObject("inviteFriendRequest/", thisChatroom);
        System.out.printf("%s成功邀请%s进入%d号聊天室\n", userAccount, friendAccount, chatroomID);
        // 广播加入的消息
        Map<String, ClientIO> clientIOHashMap = cs.clientIOMap;
        for (User user : thisChatroom.getUserHashMap().values()) {
            if (clientIOHashMap.containsKey(user.getUserAccount())) {
                ClientIO io = cs.clientIOMap.get(user.getUserAccount());
                if (!user.getUserAccount().equals(userAccount) && !user.getUserAccount().equals(friendAccount)) {
                    io.sendObject("joinChatroomRequest/", thisChatroom);
                }
                io.sendMsg(String.format("receiveChatMsg/%s/%d/%s", friendAccount, chatroomID, String.format("我是%s，一起聊聊吧！", friendName)));
            }
        }

    }

    /**
     * 用户设置头像
     *
     * @param data 从客户端发送的原始数据
     */
    private void setImage(byte[] data) {
        String[] cmd = new String(data).split("/", 3);
        String userAccount = cmd[1];
        System.out.printf("服务器收到%s的新头像\n", userAccount);
        // 获取img
        byte[] tmp = (cmd[0] + "/" + cmd[1] + "/").getBytes();
        byte[] img = new byte[data.length - tmp.length];
        System.arraycopy(data, tmp.length, img, 0, img.length);
        // System.out.println(Arrays.toString(img));
        // 设置新头像
        User user = cs.clientMap.get(userAccount);
        user.setUserImage(img);
        // 获取聊天室列表中的所有成员
        HashSet<String> sendSet = new HashSet<>();
        for (ChatRoom chatRoom : user.getChatRoomList()) {
            sendSet.addAll(chatRoom.getUserHashMap().keySet());
        }
        sendSet.remove(userAccount);
        // 给自己发送头像更改通知
        io.sendObject("receiveImageChanged/", user);
        // 给其他成员发送头像更改通知
        for (String member : sendSet) {
            ClientIO memberIO = cs.clientIOMap.get(member);
            memberIO.sendObject("receiveImageChanged/", user);
        }
    }

    /**
     * 更改聊天室名称
     *
     * @param cmd cmd = ["changeChatroomName", chatroomID, chatroomName]
     */
    private void changeChatroomName(String[] cmd) {
        // cmd = ["changeChatroomName", chatroomID, chatroomName]
        int chatroomID = Integer.parseInt(cmd[1]);
        String chatroomName = cmd[2];
        ChatRoom chatRoom = cs.chatroomHashMap.get(chatroomID);
        chatRoom.setChatroomName(chatroomName);
        // 成功更改
        io.sendMsg("changeChatroomNameResponse/success");
        // 广播
        for (User user : chatRoom.getUserHashMap().values()) {
            ClientIO clientIO = cs.clientIOMap.get(user.getUserAccount());
            clientIO.sendObject("changeChatroomNameRequest/", chatRoom);
        }
    }
}