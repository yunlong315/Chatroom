package com.example.chatroom.model.backend;

import java.io.*;
import java.net.Socket;

/**
 * 用于存储用户的socket接口，方便不同用户间的信息交互
 */
public class ClientIO {
    OutputStream outputStream = null;
    DataOutputStream dataOutputStream = null;
    InputStream inputStream = null;
    DataInputStream dataInputStream = null;
    Socket socket;

    public ClientIO(Socket socket) {
        try {
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = socket;
    }

    /**
     * 发送普通信息
     * @param str 待发送的字符串，格式为“命令/参数1/参数2/……”
     */
    public void sendMsg(String str) {
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

    /**
     * 发送 命令 参数 一个对象
     * @param str “命令/参数1/……”
     * @param obj 待发送的对象
     */
    public void sendObject(String str, Object obj) {
        try {
            // 将字符串和对象转换成byte[]
            byte[] strBytes = str.getBytes();
            byte[] objBytes;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            objBytes = bo.toByteArray();
            // 将以上两个byte[]合并成一个byte[]
            byte[] data = new byte[strBytes.length + objBytes.length];
            System.arraycopy(strBytes, 0, data, 0, strBytes.length);
            System.arraycopy(objBytes, 0, data, strBytes.length, objBytes.length);
            // 输出给客户端
            int len = data.length + 5;
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送byte数组（主要用于头像图片传输）
     * @param str “命令/参数1/参数2/……”
     * @param bytes 头像对应的byte数组
     * @return 成功返回0，出错返回-1
     */
    public int sendByteArr(String str, byte[] bytes) {
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
}
