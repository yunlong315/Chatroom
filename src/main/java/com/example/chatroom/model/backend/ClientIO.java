package com.example.chatroom.model.backend;

import java.io.*;
import java.net.Socket;

public class ClientIO {
    OutputStream outputStream = null;
    DataOutputStream dataOutputStream = null;
    InputStream inputStream = null;
    DataInputStream dataInputStream = null;
    Socket socket = null;

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
