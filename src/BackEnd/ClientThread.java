package BackEnd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
                switch (cmd[0]) {
                    case "register":
                        // cmd = ["register", id, pwd]
                        register(cmd, clientSocket);
                        System.out.println("注册成功！id: " + cmd[1]);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(String[] cmd, Socket clientSocket) {
        Map clientMap = CenterServer.getCenterServer().clientMap;
        clientMap.put(cmd[1], new ClientAccount(cmd[1], cmd[2], clientSocket));
    }
}
