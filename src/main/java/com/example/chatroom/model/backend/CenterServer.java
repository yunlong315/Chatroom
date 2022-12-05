package com.example.chatroom.model.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CenterServer {

    //    单例模式，方便其他类快速访问
    private static CenterServer cs = new CenterServer();
    ServerSocket server = null;

    private CenterServer() {}

    public static CenterServer getCenterServer() {
        return cs;
    }

    //    用户账号集合Map<String id, User user>
    Map<String, User> clientMap = Collections.synchronizedMap(new HashMap<>());
    Map<Integer, ChatRoom> chatroomHashMap = Collections.synchronizedMap(new HashMap<>());//记录聊天室

    private int listen(ServerSocket serverSocket) throws IOException {
        while (true) {
            // 循环监听，当没有Client启动时代码阻塞，有则新建一个线程
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket.getInetAddress().getHostAddress() + " SUCCESS TO CONNECT...");
            ClientThread clientThread = new ClientThread(clientSocket);
            clientThread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        cs.listen(serverSocket);
    }
}
