package com.example.chatroom.backend.socket;

import com.example.chatroom.backend.entity.ChatRoom;
import com.example.chatroom.backend.entity.User;
import com.example.chatroom.backend.reponses.*;
import com.example.chatroom.model.ChatModel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 客户端中用于与服务端进行信息传输的类
 */
public class Client {
    private static Client client;
    private final RegisterResponse registerResponse = new RegisterResponse("");
    private final LoginResponse loginResponse = new LoginResponse("");
    private final CreateChatroomResponse createChatroomResponse = new CreateChatroomResponse("");
    private final JoinChatroomResponse joinChatroomResponse = new JoinChatroomResponse("");
    private final ChatResponse chatResponse = new ChatResponse("");
    private final AddFriendResponse addFriendResponse = new AddFriendResponse("");
    private final InviteFriendResponse inviteFriendResponse = new InviteFriendResponse("");
    private final ChangeChatroomNameResponse changeChatroomNameResponse = new ChangeChatroomNameResponse("");
    private ChatModel chatModel;
    private Socket socket = null;
    private OutputStream outputStream = null;
    private DataOutputStream dataOutputStream = null;
    private InputStream inputStream = null;
    private DataInputStream dataInputStream = null;
    private Thread readThread = null;
    private boolean readThreadExit = false;
    private byte[] retByteArr;  // retByteArr用于记录服务器返回的字符串
    private String retMsg = "";  // retMsg用于记录服务器返回的字符串
    private byte[] loginResponseByteArr;
    private byte[] joinChatroomResponseByteArr;
    private byte[] joinChatroomRequestByteArr;
    private byte[] addFriendResponseByteArr;
    private byte[] addFriendRequestByteArr;
    private byte[] inviteFriendResponseByteArr;
    private byte[] inviteFriendRequestByteArr;
    private byte[] receiveImageChangedByteArr;
    private byte[] changeChatroomNameRequestByteArr;

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

    /**
     * 客户端关闭时调用该函数
     */
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

    /**
     * 初始化客户端
     *
     * @return 成功返回0，失败返回-1
     */
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
     * 向服务器发送一个字符串
     *
     * @param str 待发送的字符串，格式为“命令/参数1/参数2/……”
     * @return 发送成功返回0，失败返回-1
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

    /**
     * 向服务器发送byte数组（主要用于头像图片传输）
     *
     * @param str   “命令/参数1/参数2/……”
     * @param bytes 头像对应的byte数组
     * @return 成功返回0，出错返回-1
     */
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

    /**
     * 接收服务器发送的数据
     *
     * @return 成功返回0，失败返回-1
     */
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

    /**
     * 提取服务器发送的对象
     *
     * @param len     前置命令+参数的长度
     * @param byteArr 服务器发送的数据
     * @return 接收到的对象
     */
    private Object receiveObj(int len, byte[] byteArr) {
        byte[] objByte = new byte[byteArr.length - len];
        System.arraycopy(byteArr, len, objByte, 0, objByte.length);
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
                    System.out.println("receive: " + retMsg.substring(0, Math.min(30, retMsg.length())));
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
                            joinChatroomRequest();
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
                        case "inviteFriendResponse":
                            synchronized (inviteFriendResponse) {
                                inviteFriendResponse.setTmpMsg(retMsg);
                                inviteFriendResponseByteArr = retByteArr;
                                inviteFriendResponse.notify();
                            }
                            break;
                        case "inviteFriendRequest":
                            inviteFriendRequestByteArr = retByteArr;
                            inviteFriendRequest();
                            break;
                        case "receiveImageChanged":
                            receiveImageChangedByteArr = retByteArr;
                            receiveImageChanged();
                            break;
                        case "changeChatroomNameResponse":
                            synchronized (changeChatroomNameResponse) {
                                changeChatroomNameResponse.setTmpMsg(retMsg);
                                changeChatroomNameResponse.notify();
                            }
                            break;
                        case "changeChatroomNameRequest":
                            changeChatroomNameRequestByteArr = retByteArr;
                            changeChatroomNameRequest();
                            break;
                        default:
                            System.out.println(cmd + " not found");
                            break;
                    }
                }
            }
        });
        readThread.start();
    }

    /**
     * 用户注册方法（客户端用）
     *
     * @param userAccount  用户账号
     * @param userPassWord 用户密码
     * @param userName     用户名称
     * @return RegisterResponse
     */
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

    /**
     * 用户登录方法（客户端用）
     *
     * @param userAccount  用户账户
     * @param userPassWord 用户密码
     * @return LoginResponse
     */
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
            user = (User) receiveObj(len, loginResponseByteArr);
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

    /**
     * 创建聊天室方法（客户端用）
     *
     * @param user 用户本身
     * @return CreateChatroomResponse
     */
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
            chatRoom.getUserHashMap().put(user.getUserAccount(), user);
            return new CreateChatroomResponse(chatRoom);
        } else {
            return new CreateChatroomResponse(args[1]);
        }
    }

    /**
     * 用户发消息方法（客户端用）
     *
     * @param message     聊天内容
     * @param userAccount 用户账号
     * @param chatroomID  聊天室ID
     * @return ChatResponse
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

    /**
     * 用户接收聊天室消息方法（服务端用）
     *
     * @param retMsg 服务器返回数据
     */
    private void receiveChatMsg(String retMsg) {
        // args = ["receiveChatMsg", sender, chatroomID, chatContents]
        String[] args = retMsg.split("/", 4);
        String sender = args[1];
        int chatroomID = Integer.parseInt(args[2]);
        String chatContents = args[3];
        chatModel.receiveMsg(sender, chatroomID, chatContents);
    }

    /**
     * 用户加入聊天室方法（客户端用）
     *
     * @param userAccount 用户账号
     * @param chatroomID  聊天室ID
     * @return JoinChatroomResponse
     */
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
            chatRoom = (ChatRoom) receiveObj(len, joinChatroomResponseByteArr);
            if (chatRoom == null) {
                return new JoinChatroomResponse("接收服务器发送对象失败");
            }
            return new JoinChatroomResponse(chatRoom);
        } else {
            return new JoinChatroomResponse(args[1]);
        }
    }

    /**
     * 某用户加入聊天室后其他用户收到更新通知
     */
    private void joinChatroomRequest() {
        // args = ["joinChatroomRequest", (ChatRoom chatroom)
        int len = "joinChatroomRequest/".getBytes().length;
        ChatRoom chatroom = (ChatRoom) receiveObj(len, joinChatroomRequestByteArr);
        chatModel.updateChatroom(chatroom);
    }

    /**
     * 用户添加好友方法（客户端用）
     *
     * @param userAccount   用户账号
     * @param friendAccount 申请好友的账号
     * @return AddFriendResponse
     */
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
            user = (User) receiveObj(len, addFriendResponseByteArr);
            if (user == null) {
                return new AddFriendResponse("接收服务器发送对象失败");
            }
            return new AddFriendResponse(user);
        } else {
            return new AddFriendResponse(args[1]);
        }
    }

    /**
     * 用户收到添加好友请求（服务端用）
     */
    private void addFriendRequest() {
        int len = "addFriendRequest/success/".getBytes().length;
        User user = (User) receiveObj(len, addFriendRequestByteArr);
        chatModel.beAddedAsFriend(user);
    }

    /**
     * 邀请好友进入聊天室方法（客户端用）
     *
     * @param userAccount   用户账号
     * @param friendAccount 好友账号
     * @param chatroomID    聊天室ID
     * @return InviteFriendResponse
     */
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

    /**
     * 用户收到邀请进聊天室的请求（服务端用）
     */
    private void inviteFriendRequest() {
        // args = ["inviteFriendRequest", (ChatRoom chatRoom)]
        int len = "inviteFriendRequest/".getBytes().length;
        ChatRoom chatRoom = (ChatRoom) receiveObj(len, inviteFriendRequestByteArr);
        chatModel.beInvitedToRoom(chatRoom);
    }

    /**
     * 设置用户头像
     *
     * @param userAccount 用户账号
     * @param imagePath   图片路径
     * @throws IOException 文件操作失败
     */
    public void setImage(String userAccount, String imagePath) throws IOException {
        byte[] image = Files.readAllBytes(Path.of(imagePath));
        // System.out.println(Arrays.toString(image));
        String str = String.format("setImage/%s/", userAccount);
        sendByteArr(str, image);
    }

    /**
     * 其他用户接受某用户头像更改并及时更新（服务端用）
     */
    private void receiveImageChanged() {
        int len = "receiveImageChanged/".getBytes().length;
        User user = (User) receiveObj(len, receiveImageChangedByteArr);
        System.out.printf("客户端接收到%s的头像更新\n", user.getUserName());
        // System.out.println(Arrays.toString(user.getUserImage()));
        chatModel.updateUser(user);
    }

    /**
     * 更改聊天室名称（客户端用）
     *
     * @param chatroomID   聊天室ID
     * @param chatroomName 新的聊天室名称
     * @return ChangeChatroomNameResponse
     */
    public ChangeChatroomNameResponse changeChatroomName(int chatroomID, String chatroomName) {
        String str = String.format("changeChatroomName/%d/%s", chatroomID, chatroomName);
        sendMsg(str);
        return getChangeChatroomNameResponse();
    }

    private ChangeChatroomNameResponse getChangeChatroomNameResponse() {
        // args = ["changeChatroomNameResponse", "success"/errorMsg]
        synchronized (changeChatroomNameResponse) {
            try {
                changeChatroomNameResponse.wait(5000);
                if (changeChatroomNameResponse.getTmpMsg().equals("")) {
                    changeChatroomNameResponse.setTmpMsg("changeChatroomNameResponse/响应超时，请检查网络");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] args = changeChatroomNameResponse.getTmpMsg().split("/");
        changeChatroomNameResponse.setTmpMsg("");
        if (args[1].equals("success")) {
            return new ChangeChatroomNameResponse(true);
        } else {
            return new ChangeChatroomNameResponse(args[1]);
        }
    }

    /**
     * 用户接收到聊天室名称更改并及时更新（服务端用）
     */
    private void changeChatroomNameRequest() {
        int len = "changeChatroomNameRequest/".getBytes().length;
        ChatRoom chatRoom = (ChatRoom) receiveObj(len, changeChatroomNameRequestByteArr);
        chatModel.updateChatroom(chatRoom);
    }
}
