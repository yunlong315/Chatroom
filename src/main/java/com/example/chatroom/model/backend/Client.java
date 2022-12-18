package com.example.chatroom.model.backend;

import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.backend.reponses.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Client {
    private static Client client;
    private ChatModel chatModel;

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

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
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
    private byte[] retByteArr;  // retByteArr用于记录服务器返回的字符串
    private String retMsg = "";  // retMsg用于记录服务器返回的字符串
    private final RegisterResponse registerResponse = new RegisterResponse("");
    private final LoginResponse loginResponse = new LoginResponse("");
    private byte[] loginResponseByteArr;
    private final CreateChatroomResponse createChatroomResponse = new CreateChatroomResponse("");
    private final JoinChatroomResponse joinChatroomResponse = new JoinChatroomResponse("");
    private byte[] joinChatroomResponseByteArr;
    private byte[] joinChatroomRequestByteArr;
    private final ChatResponse chatResponse = new ChatResponse("");
    private final AddFriendResponse addFriendResponse = new AddFriendResponse("");
    private byte[] addFriendResponseByteArr;
    private byte[] addFriendRequestByteArr;
    private final InviteFriendResponse inviteFriendResponse = new InviteFriendResponse("");
    private byte[] inviteFriendResponseByteArr;
    private byte[] receiveImageChangedByteArr;
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

    /**
     * 向服务器发送一个字符串。发送成功返回0，失败返回-1。
     *
     * @param str
     * @return int
     */
    private int sendMsg(String str) {
        try {
            byte[] data = str.getBytes();
            int len = data.length + 5;
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int sendByteArr(String str, byte[] bytes) {
        try {
            byte[] strBytes = str.getBytes();
            // 将以上两个byte[]合并成一个byte[]
            byte[] data = new byte[strBytes.length + bytes.length];
            System.arraycopy(strBytes, 0, data, 0, strBytes.length);
            System.arraycopy(bytes, 0, data, strBytes.length, bytes.length);
            // 输出给客户端
            int len = data.length + 5;
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int receiveMsg() {
        try {
            // 先读取一个整数表示数据长度，再读取该长度的数据，保证不粘包丢包
            int len = dataInputStream.readInt();
            byte[] data = new byte[len - 5];
            dataInputStream.readFully(data);
            retByteArr = data;
            retMsg = new String(data);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private Object receiveObj(int len, byte[] addFriendResponseByteArr) {
        byte[] objByte = new byte[addFriendResponseByteArr.length - len];
        System.arraycopy(addFriendResponseByteArr, len, objByte, 0, objByte.length);
        Object obj = null;
        try {
            //bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(objByte);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 新建一个专门接受服务器消息的线程。
     */
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
                    String cmd = retMsg.substring(0, retMsg.indexOf('/'));
                    switch (cmd) {
                        case "registerResponse":
                            // 将服务器返回的数据存储到registerRetMsg中
                            synchronized (registerResponse) {
                                registerResponse.setTmpMsg(retMsg);
                                registerResponse.notify();
                            }
                            break;
                        case "loginResponse":
                            synchronized (loginResponse) {
                                loginResponse.setTmpMsg(retMsg);
                                loginResponseByteArr = retByteArr;
                                loginResponse.notify();
                            }
                            break;
                        case "createChatroomResponse":
                            synchronized (createChatroomResponse) {
                                createChatroomResponse.setTmpMsg(retMsg);
                                createChatroomResponse.notify();
                            }
                            break;
                        case "joinChatroomResponse":
                            synchronized (joinChatroomResponse) {
                                joinChatroomResponse.setTmpMsg(retMsg);
                                joinChatroomResponseByteArr = retByteArr;
                                joinChatroomResponse.notify();
                            }
                            break;
                        case "joinChatroomRequest":
                            joinChatroomRequestByteArr = retByteArr;
                            joinChatroomRequest(retMsg);
                            break;
                        case "chatResponse":
                            synchronized (chatResponse) {
                                chatResponse.setTmpMsg(retMsg);
                                chatResponse.notify();
                            }
                            break;
                        case "receiveChatMsg":
                            receiveChatMsg(retMsg);
                            break;
                        case "addFriendResponse":
                            synchronized (addFriendResponse) {
                                addFriendResponse.setTmpMsg(retMsg);
                                addFriendResponseByteArr = retByteArr;
                                addFriendResponse.notify();
                            }
                            break;
                        case "addFriendRequest":
                            addFriendRequestByteArr = retByteArr;
                            addFriendRequest();
                            break;
                        case "inviteFriend":
                            synchronized (inviteFriendResponse) {
                                inviteFriendResponse.setTmpMsg(retMsg);
                                inviteFriendResponseByteArr = retByteArr;
                                inviteFriendResponse.notify();
                            }
                            break;
                        case "inviteFriendRequest":
                            inviteFriendRequest(retMsg);
                            break;
                        case "receiveImageChanged":
                            receiveImageChangedByteArr = retByteArr;
                            receiveImageChanged();
                            break;
                    }
                }
            }
        });
        readThread.start();
    }

    public RegisterResponse register(String userAccount, String userPassWord, String userName) {
        String str = "register/" + userAccount + "/" + userPassWord + "/" + userName;
        // 向服务器发送注册数据，失败处理交给getRegisterResponse()方法
        sendMsg(str);
        return getRegisterResponse();
    }

    private RegisterResponse getRegisterResponse() {
        // 进入等待状态，直到其他方法调用 registerResponse.notify() 函数
        synchronized (registerResponse) {
            try {
                registerResponse.wait(5000);
                if (registerResponse.getTmpMsg().equals("")) {
                    registerResponse.setTmpMsg("registerResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String msg = registerResponse.getTmpMsg().split("/")[1];
        registerResponse.setTmpMsg("");  // 将registerResponse还原为初始态，方便下一次使用
        if (msg.equals("success")) {
            return new RegisterResponse(true);
        } else {
            return new RegisterResponse(msg);
        }
    }

    public LoginResponse login(String userAccount, String userPassWord) {
        String str = "login/" + userAccount + "/" + userPassWord;
        // 向服务器发送注册数据
        sendMsg(str);
        return getLoginResponse();
    }

    private LoginResponse getLoginResponse() {
        // args: ["loginResponse", "success"/errorMsg, (User user)]
        // 进入等待状态，直到其他方法调用 loginResponse.notify() 函数
        synchronized (loginResponse) {
            try {
                loginResponse.wait(5000);
                if (loginResponse.getTmpMsg().equals("")) {
                    loginResponse.setTmpMsg("loginResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = loginResponse.getTmpMsg().split("/");
        loginResponse.setTmpMsg("");
        // 判断是否注册成功
        if (args[1].equals("success")) {
            int len = "loginResponse/success/".getBytes().length;
            User user = null;
            user = (User)receiveObj(len, loginResponseByteArr);
            if (user == null) {
                return new LoginResponse("接收服务器发送对象失败");
            }
            for (ChatRoom chatRoom : user.getChatRoomList()) {
                System.out.println("chatroom: " + chatRoom.getID());
            }
            System.out.println("login success");
            return new LoginResponse(user);
        } else {
            return new LoginResponse(args[1]);
        }
    }

    public CreateChatroomResponse createChatroom(User user) {
        String str = "createChatroom/" + user.getUserAccount();
        // 向服务器发送注册数据
        sendMsg(str);
        return getCreateChatroomResponse(user);
    }

    private CreateChatroomResponse getCreateChatroomResponse(User user) {
        // args: ["createChatroomRetMsg", "success"/errorMsg, (chatroomId)]
        // 进入等待状态，直到其他方法调用 loginResponse.notify() 函数
        synchronized (createChatroomResponse) {
            try {
                createChatroomResponse.wait(5000);
                if (createChatroomResponse.getTmpMsg().equals("")) {
                    createChatroomResponse.setTmpMsg("registerResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = createChatroomResponse.getTmpMsg().split("/");
        createChatroomResponse.setTmpMsg("");
        // 判断是否注册成功
        if (args[1].equals("success")) {
            ChatRoom chatRoom = new ChatRoom(Integer.parseInt(args[2]));
            chatRoom.userHashMap.put(user.getUserAccount(), user);
            return new CreateChatroomResponse(chatRoom);
        } else {
            return new CreateChatroomResponse(args[1]);
        }
    }

    /**
     * 向服务器发送聊天信息。
     */
    public ChatResponse chat(String message, String userAccount, int chatroomID) {
        String str = String.format("chat/%s/%d/%s", userAccount, chatroomID, message);
        // 向服务器发送聊天信息
        if (sendMsg(str) == -1) {
            return new ChatResponse("发送失败");
        }
        return getChatResponse();
    }

    public ChatResponse getChatResponse() {
        // args = ["chatResponse", "success"/errorMsg]
        synchronized (chatResponse) {
            try {
                chatResponse.wait(5000);
                if (chatResponse.getTmpMsg().equals("")) {
                    chatResponse.setTmpMsg("chatResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = chatResponse.getTmpMsg().split("/");
        chatResponse.setTmpMsg("");
        String msg = args[1];
        chatResponse.setTmpMsg("");  // 将registerResponse还原为初始态，方便下一次使用
        if (msg.equals("success")) {
            return new ChatResponse(true);
        } else {
            return new ChatResponse(msg);
        }
    }

    public JoinChatroomResponse joinChatroom(String userAccount, int chatroomID) {
        String str = String.format("joinChatroom/%s/%d", userAccount, chatroomID);
        // 向服务器发送加入聊天室数据
        sendMsg(str);
        return getJoinChatroomResponse();
    }

    private JoinChatroomResponse getJoinChatroomResponse() {
        // args = ["joinChatroomResponse", "success"/errorMsg, (Chatroom chatroom)]
        synchronized (joinChatroomResponse) {
            try {
                joinChatroomResponse.wait(5000);
                if (joinChatroomResponse.getTmpMsg().equals("")) {
                    joinChatroomResponse.setTmpMsg("joinChatroomResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = joinChatroomResponse.getTmpMsg().split("/");
        joinChatroomResponse.setTmpMsg("");
        if (args[1].equals("success")) {
            int len = "joinChatroomResponse/success/".getBytes().length;
            ChatRoom chatRoom = null;
            chatRoom = (ChatRoom)receiveObj(len, joinChatroomResponseByteArr);
            if (chatRoom == null) {
                return new JoinChatroomResponse("接收服务器发送对象失败");
            }
            return new JoinChatroomResponse(chatRoom);
        } else {
            return new JoinChatroomResponse(args[1]);
        }
    }

    private void joinChatroomRequest(String retMsg) {
        // args = ["joinChatroomRequest", (ChatRoom chatroom)
        int len = "joinChatroomRequest/".getBytes().length;
        ChatRoom chatroom = (ChatRoom) receiveObj(len, joinChatroomRequestByteArr);
        chatModel.updateChatroom(chatroom);
    }

    private void receiveChatMsg(String retMsg) {
        // args = ["receiveChatMsg", sender, chatroomID, chatContents]
        String[] args = retMsg.split("/", 4);
        String sender = args[1];
        int chatroomID = Integer.parseInt(args[2]);
        String chatContents = args[3];
        chatModel.receiveMsg(sender,chatroomID,chatContents);
    }

    public AddFriendResponse addFriend(String userAccount, String friendAccount) {
        String str = "addFriend/" + userAccount + "/" + friendAccount;
        sendMsg(str);
        return getAddFriendResponse();
    }

    private AddFriendResponse getAddFriendResponse() {
        // args = ["addFriendResponse", "success"/errorMsg, (User user)]
        synchronized (addFriendResponse) {
            try {
                addFriendResponse.wait(5000);
                if (addFriendResponse.getTmpMsg().equals("")) {
                    addFriendResponse.setTmpMsg("addFriendResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = addFriendResponse.getTmpMsg().split("/");
        addFriendResponse.setTmpMsg("");
        if (args[1].equals("success")) {
            int len = "addFriendResponse/success/".getBytes().length;
            User user = null;
            user = (User)receiveObj(len, addFriendResponseByteArr);
            if (user == null) {
                return new AddFriendResponse("接收服务器发送对象失败");
            }
            return new AddFriendResponse(user);
        } else {
            return new AddFriendResponse(args[1]);
        }
    }

    private void addFriendRequest() {
        User user = null;
        int len = "addFriendRequest/success/".getBytes().length;
        user = (User) receiveObj(len, addFriendRequestByteArr);
        chatModel.beAddedAsFriend(user);
    }

    public InviteFriendResponse inviteFriend(String userAccount, String friendAccount, int chatroomID) {
        String str = String.format("inviteFriend/%s/%s/%d", userAccount, friendAccount, chatroomID);
        sendMsg(str);
        return getInviteFriendResponse();
    }

    private InviteFriendResponse getInviteFriendResponse() {
        // args = ["inviteFriendResponse", "success"/errorMsg, (ChatRoom chatroom)]
        synchronized (inviteFriendResponse) {
            try {
                inviteFriendResponse.wait(5000);
                if (inviteFriendResponse.getTmpMsg().equals("")) {
                    inviteFriendResponse.setTmpMsg("inviteFriendResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = inviteFriendResponse.getTmpMsg().split("/");
        inviteFriendResponse.setTmpMsg("");
        if (args[1].equals("success")) {
            int len = "inviteFriendResponse/success/".getBytes().length;
            ChatRoom chatroom = null;
            chatroom = (ChatRoom) receiveObj(len, inviteFriendResponseByteArr);
            if (chatroom == null) {
                return new InviteFriendResponse("接收服务器发送对象失败");
            }
            return new InviteFriendResponse(chatroom);
        } else {
            return new InviteFriendResponse(args[1]);
        }
    }

    private void inviteFriendRequest(String retMsg) {
        // args = ["inviteFriendRequest", userAccount, chatroomID]
        String[] args = retMsg.split("/");
        String userAccount = args[1];
        int chatroomID = Integer.parseInt(args[2]);
        // 调用joinChatroom()方法并获得JoinChatroomResponse
        JoinChatroomResponse joinChatroomResponse1 = joinChatroom(userAccount, chatroomID);
        chatModel.updateChatroom(joinChatroomResponse1.getChatroom());
    }

    /**
     * 设置用户头像
     * @param userAccount
     * @param imagePath-图片路径
     * @throws IOException
     */
    public void setImage(String userAccount, String imagePath) throws IOException {
         byte[] image = Files.readAllBytes(Path.of(imagePath));
         String str = String.format("setImage/%s/", userAccount);
         sendByteArr(str, image);
    }

    private void receiveImageChanged() {
        int len = "receiveImageChanged/".getBytes().length;
        User user = (User) receiveObj(len, receiveImageChangedByteArr);
        // TODO: 更新头像
    }
}
