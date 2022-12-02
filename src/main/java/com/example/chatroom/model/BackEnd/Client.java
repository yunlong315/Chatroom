package com.example.chatroom.model.BackEnd;

import com.example.chatroom.model.BackEnd.reponses.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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
    InputStream inputStream = null;
    DataInputStream dataInputStream = null;
    // retMsg用于记录服务器返回的字符串
    String retMsg = null;

    public int init() {
        try {
            InetAddress serverIP = InetAddress.getByName("127.0.0.1");
            int port = 8888;
            socket = new Socket(serverIP, port);

            // 得到socket输出流
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);

            // 得到socket输入流
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int sendMsg(String str) {
        try {
            byte[] data = str.getBytes();
            int len = data.length + 5;
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private int receiveMsg() {
        try {
            int len = dataInputStream.readInt();
            byte[] data = new byte[len - 5];
            dataInputStream.readFully(data);
            retMsg = new String(data);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public RegisterResponse register(String userAccount, String userPassWord, String userName) {
        String str = "register/" + userAccount + "/" + userPassWord + "/" + userName;
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            return new RegisterResponse("向服务器发送数据失败");
        }
        // 接受接受服务器返回数据
        if (receiveMsg() == -1) {
            return new RegisterResponse("接受服务器返回数据失败");
        }
        // 判断是否注册成功
        if (retMsg.equals("success")) {
            return new RegisterResponse(true);
        } else {
            return new RegisterResponse(retMsg);
        }
    }

    public LoginResponse login(String userAccount, String userPassWord) {
        String str = "login/" + userAccount + "/" + userPassWord;
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            return new LoginResponse("向服务器发送数据失败");
        }
        // 接受接受服务器返回数据
        if (receiveMsg() == -1) {
            return new LoginResponse("接受服务器返回数据失败");
        }
        // 判断是否注册成功
        if (retMsg.equals("success")) {
            try {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                User user = (User) input.readObject();
                user.setUserSocket(socket);
                return new LoginResponse(user);
            } catch (Exception e) {
                e.printStackTrace();
                return new LoginResponse("接受服务器返回对象失败");
            }
        } else {
            return new LoginResponse(retMsg);
        }
    }

    public CreateChatroomResponse createChatroom() {
        String str = "createChatroom/";
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            return new CreateChatroomResponse("向服务器发送数据失败");
        }
        // 接受接受服务器返回数据
        if (receiveMsg() == -1) {
            return new CreateChatroomResponse("接受服务器返回数据失败");
        }
        if (retMsg.equals("success")) {
            try {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                User user = (User) input.readObject();
                user.setUserSocket(socket);
                ChatRoom chatroom = (ChatRoom) input.readObject();
                //?
                return new CreateChatroomResponse(chatroom);
            } catch (Exception e) {
                e.printStackTrace();
                return new CreateChatroomResponse("接受服务器返回对象失败");
            }
        } else {
            return new CreateChatroomResponse(retMsg);
        }
    }


    public ChatResponse chat(String message) {
        String str = "login/";
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            return new ChatResponse("向服务器发送数据失败");
        }
        // 接受接受服务器返回数据
        if (receiveMsg() == -1) {
            return new ChatResponse("接受服务器返回数据失败");
        }
        if (retMsg.equals("success")) {
            try {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                User user = (User) input.readObject();
                user.setUserSocket(socket);
                ChatRoom chatroom = (ChatRoom) input.readObject();

                return new ChatResponse(user, chatroom);
            } catch (Exception e) {
                e.printStackTrace();
                return new ChatResponse("接受服务器返回对象失败");
            }
        } else {
            return new ChatResponse(retMsg);
        }
    }

    public JoinChatroomResponse joinChatroom() {
        String str = "addChatroom/";
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            return new JoinChatroomResponse("向服务器发送数据失败");
        }
        // 接受接受服务器返回数据
        if (receiveMsg() == -1) {
            return new JoinChatroomResponse("接受服务器返回数据失败");
        }
        if (retMsg.equals("success")) {
            try {
                ObjectInputStream input = new ObjectInputStream(inputStream);
                User user = (User) input.readObject();
                user.setUserSocket(socket);
                ChatRoom chatroom = (ChatRoom) input.readObject();
                return new JoinChatroomResponse(user, chatroom);
            } catch (Exception e) {
                e.printStackTrace();
                return new JoinChatroomResponse("接受服务器返回对象失败");
            }
        } else {
            return new JoinChatroomResponse(retMsg);
        }
    }


}
