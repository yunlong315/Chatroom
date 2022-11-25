package BackEnd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class CenterServer {

//    单例模式，方便其他类快速访问
    private static CenterServer cs = new CenterServer();

    private CenterServer() {
    }

    public static CenterServer getCenterServer() {
        return cs;
    }

//    用户账号集合Map<String id, ClientAccount clientAccount>
    Map clientMap = Collections.synchronizedMap(new HashMap());

    private int listen(ServerSocket serverSocket) throws IOException {
        while (true) {
//            循环监听，当没有Client启动时代码阻塞，有则新建一个线程
            Socket clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket);
            clientThread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        cs.listen(serverSocket);
    }
}