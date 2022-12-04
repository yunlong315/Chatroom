package com.example.chatroom.model.backend;

import com.example.chatroom.model.backend.reponses.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static Client client;

    private Client() {
        // readFromCenterServer()新建了一个线程，用于不断获取从服务器发送而来的数据，Client被构造时即开始运行
        readFromCenterServer();
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    private void close() {
        try {
            System.out.println("client close");
            inputStream.close();
            dataInputStream.close();
            outputStream.close();
            dataInputStream.close();
            readThreadExit = true;
            readThread.interrupt();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Socket socket = null;
    private OutputStream outputStream = null;
    private DataOutputStream dataOutputStream = null;
    private InputStream inputStream = null;
    private DataInputStream dataInputStream = null;
    private Thread readThread = null;
    private boolean readThreadExit = false;

    private String retMsg = "";  // retMsg用于记录服务器返回的字符串
    private String registerRetMsg = "";  // registerRetMsg用于记录register操作后服务器返回的结果，下同
    private String loginRetMsg = "";
    private String createChatroomRetMsg = "";
    private String chatRetMsg = "";

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

    private void readFromCenterServer() {
        System.out.println("Listening to the CenterServer...");
        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!readThreadExit) {
                    if (receiveMsg() == -1) {
                        continue;
                    }
                    System.out.println("receive: " + retMsg);
                    String[] cmd = retMsg.split("/");
                    switch (cmd[0]) {
                        case "registerResponse":
                            // 将服务器返回的数据存储到registerRetMsg中
                            registerRetMsg = retMsg;
                            break;
                        case "loginResponse":
                            loginRetMsg = retMsg;
                            break;
                        case "createChatroomResponse":
                            createChatroomRetMsg = retMsg;
                            break;
                        case "chat":
                            chatRetMsg = retMsg;
                            break;
                    }
                }
            }
        });
        readThread.start();
    }

    public void register(String userAccount, String userPassWord, String userName) {
        String str = "register/" + userAccount + "/" + userPassWord + "/" + userName;
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            registerRetMsg = "register/向服务器发送数据失败";
        }
    }

    public RegisterResponse registerResponse() {
        String msg = registerRetMsg.split("/")[1];
        if (msg.equals("success")) {
            return new RegisterResponse(true);
        } else {
            return new RegisterResponse(msg);
        }
    }

    public void login(String userAccount, String userPassWord) {
        String str = "login/" + userAccount + "/" + userPassWord;
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            loginRetMsg = "login/向服务器发送数据失败";
        }
    }

    public LoginResponse loginResponse() {
        // args: ["loginResponse", "success"/errorMsg, (userAccount, pwd, userName)]
        String[] args = loginRetMsg.split("/");
        // 判断是否注册成功
        if (args[1].equals("success")) {
            return new LoginResponse(new User(args[2], args[3], args[4], socket));
        } else {
            return new LoginResponse(args[1]);
        }
    }

    public void createChatroom(String userAccount) {
        String str = "createChatroom/" + userAccount;
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            createChatroomRetMsg = "createChatroomRetMsg/向服务器发送数据失败";
        }
    }

    public CreateChatroomResponse createChatroomResponse(User user) {
        // args: ["createChatroomRetMsg", "success"/errorMsg, (chatroomId)]
        String[] args = createChatroomRetMsg.split("/");
        // 判断是否注册成功
        if (args[1].equals("success")) {
            ChatRoom chatRoom = new ChatRoom(Integer.parseInt(args[2]));
            chatRoom.userHashMap.put(user.getUserAccount(), user);
            return new CreateChatroomResponse(chatRoom);
        } else {
            return new CreateChatroomResponse(args[1]);
        }
    }


    public void chat(String message, User user, ChatRoom chatRoom) {
        String str = String.format("chat/%s/%d/%s", user.getUserAccount(), chatRoom.getID(), message);
        // 向服务器发送注册数据
        if (sendMsg(str) == -1) {
            chatRetMsg = "chat/向服务器发送数据失败";
        }
    }

    public ChatResponse chatResponse(String message, User user, ChatRoom chatRoom) {
        String[] args = chatRetMsg.split("/");
        if (args[1].equals("success")) {
            return new ChatResponse(user, chatRoom, message);
        } else {
            return new ChatResponse(args[1]);
        }
    }

    public JoinChatroomResponse joinChatroom(String userAccount, int chatroomID) {
        String str = String.format("joinChatroom/%s/%d", userAccount, chatroomID);
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