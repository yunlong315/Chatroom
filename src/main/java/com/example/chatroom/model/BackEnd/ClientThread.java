package com.example.chatroom.model.BackEnd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ClientThread extends Thread {

    private Socket clientSocket = null;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            while (true) {
                // 获取Client发送的数据，格式为”数据类型+数据长度+数据“
                byte b = dataInputStream.readByte();
                int len = dataInputStream.readInt();
                byte[] data = new byte[len - 5];
                dataInputStream.readFully(data);
                String[] cmd = new String(data).split("/");
//                switch (cmd[0]) {
//                    case "register":
//                        // cmd = ["register", id, pwd]
//                        register(cmd, clientSocket);
//                        System.out.println("注册成功！id: " + cmd[1]);
//                        break;
//                }
            }
        } catch (SocketException e) {
            System.out.println("User logout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
