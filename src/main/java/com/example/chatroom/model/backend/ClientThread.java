package com.example.chatroom.model.backend;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientThread extends Thread {

    private Socket clientSocket = null;
    OutputStream outputStream = null;
    DataOutputStream dataOutputStream = null;
    InputStream inputStream = null;
    DataInputStream dataInputStream = null;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        try {
            // 得到socket输出流
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);

            // 得到socket输入流
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 获取Client发送的数据，格式为”数据类型+数据长度+数据“
                int len = dataInputStream.readInt();
                byte[] data = new byte[len - 5];
                dataInputStream.readFully(data);
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
                        chat(cmd);
                        break;

                }
            }
        } catch (Exception e) {
            System.out.println("User logout");
        }
    }

    private void sendMsg(String str) {
        try {
            byte[] data = str.getBytes();
            int len = data.length + 5;
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register(String[] cmd) {
        // cmd = ["register", userAccount, pwd, userName]
        Map<String, User> clientMap = CenterServer.getCenterServer().clientMap;
        String userAccount = cmd[1];
        if (clientMap.containsKey(userAccount)) {
            System.out.println("账号重复");
            sendMsg("registerResponse/账号重复");
        } else {
            clientMap.put(userAccount, new User(userAccount, cmd[2], cmd[3], clientSocket));
            /*System.out.println(userAccount);
            for (Map.Entry<String, User> entry : clientMap.entrySet()) {
                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            }*/
            sendMsg("registerResponse/success");
        }
    }

    private void login(String[] cmd) {
        // cmd = ["login", userAccount, pwd]
        Map<String, User> clientMap = CenterServer.getCenterServer().clientMap;
        if (!clientMap.containsKey(cmd[1])) {
            // 账号不存在
            System.out.println("账号不存在");
            sendMsg("loginResponse/账号不存在");
        } else if (!clientMap.get(cmd[1]).getUserPassWord().equals(cmd[2])) {
            // 账号密码错误
            System.out.println("账号密码错误");
            sendMsg("loginResponse/账号密码错误");
        } else {
            // 登录成功
            sendMsg("loginResponse/success/" + cmd[1] + "/" + cmd[2] + "/" + clientMap.get(cmd[1]).getUserName());
            System.out.println("登录成功! 账号为: " + cmd[1]);
        }
    }

    //所有用户都可以创建chatroom，是非特定人所有的，即没有群主之分，群被创建即有一个特定id，所有人可以通过搜索id加入群聊
    //userAccount应该要从前端获取传递参数到后端
    public void createChatroom(String[] cmd) {
        // cmd = ["createChatroom", userAccount]
        int chatroomID = ChatRoom.nextID++;
        String userAccount = cmd[1];
        ChatRoom newChatroom = new ChatRoom(chatroomID);
        Map<String, User> clientMap = CenterServer.getCenterServer().clientMap;
        User thisUser = clientMap.get(userAccount);
        newChatroom.userHashMap.put(userAccount, thisUser);
        System.out.println("创建聊天室成功，聊天室ID为：" + chatroomID);
        sendMsg("createChatroomResponse/success/" + chatroomID);
    }

    //用户通过搜索chatroomID加入聊天室
    public void joinChatroom(String[] cmd) {
        // cmd = ["addChatroom", userAccount,chatroomID]

        String userAccount = cmd[1];
        String chatroomID = cmd[2];
        HashMap<String, ChatRoom> chatroomHashMap = CenterServer.getCenterServer().chatroomHashMap;
        if (!chatroomHashMap.containsKey(chatroomID)) {
            sendMsg("该聊天室ID不存在");
            return;
        }
        Map<String, User> clientMap = CenterServer.getCenterServer().clientMap;
        User thisUser = clientMap.get(userAccount);
        //?
        ChatRoom thisChatroom = chatroomHashMap.get(chatroomID);
        thisChatroom.userHashMap.put(userAccount, thisUser);
        sendMsg("加入聊天室成功");
    }

    //userAccount发送到chatroom
    public void chat(String[] cmd) throws IOException {
        // cmd = ["chat", userAccount,chatroomID,chatcontents]
        String chatContents = cmd[3];
        String userAccount = cmd[1];
        String chatroomID = cmd[2];
        //获取聊天室
        HashMap<String, ChatRoom> chatroomHashMap = CenterServer.getCenterServer().chatroomHashMap;
        ChatRoom thisChatroom = chatroomHashMap.get(chatroomID);
        //获取所有成员（包括自己）
        HashMap<String, User> userHashMap = thisChatroom.userHashMap;
        for (String userID : userHashMap.keySet()) {
            User userInChatroom = userHashMap.get(userID);
            Socket userInChatroomSocket = userInChatroom.getUserSocket();
            ClientChat clientChat = new ClientChat(userInChatroomSocket, chatContents);
            clientChat.start();
        }
    }

}