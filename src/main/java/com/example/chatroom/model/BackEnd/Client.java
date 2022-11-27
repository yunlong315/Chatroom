package com.example.chatroom.model.BackEnd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class Client {
    private static Client client;

    private Client() {
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    Socket socket = null;
    OutputStream outputStream = null;
    DataOutputStream dataOutputStream = null;

    public int init() {
        try {
            InetAddress serverIP = InetAddress.getByName("10.193.108.25");
            int port = 8888;
            socket = new Socket(serverIP, port);
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public RegisterResponse register(String userAccount, String userPassWord, String userName) {
        Map<String, User> clientMap = CenterServer.getCenterServer().clientMap;
        if (clientMap.containsKey(userAccount)) {
            return new RegisterResponse("userAccount duplicated");
        } else {
            clientMap.put(userAccount, new User(userAccount, userPassWord, userName, socket));
            System.out.println(userAccount + userPassWord);
            for (Map.Entry<String, User> entry : clientMap.entrySet()) {
                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            }
            return new RegisterResponse(true);
        }
//        try {
//            String str = "register/" + userAccount + "/" + userPassWord + "/" + userName;
//            int type = 1; // 数据类型1为String，其他的待定
//            byte[] data = str.getBytes();
//            int len = data.length + 5;
//            dataOutputStream.writeByte(type);
//            dataOutputStream.writeInt(len);
//            dataOutputStream.write(data);
//            dataOutputStream.flush();
//        } catch (Exception e) {
//            return -1;
//        }
//        return 0;
    }
}
