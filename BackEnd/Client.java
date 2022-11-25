package BackEnd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

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
            return -1;
        }
        return 0;
    }

//    确认密码以及判断id是否重复交给前端吧！
    public int register(String id, String pwd) {
        try {
            String str = "register/" + id + "/" + pwd;
            int type = 1; // 数据类型1为String，其他的待定
            byte[] data = str.getBytes();
            int len = data.length + 5;
            dataOutputStream.writeByte(type);
            dataOutputStream.writeInt(len);
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
        client.register("20375102", "123123");
        while (true){;}
    }
}
