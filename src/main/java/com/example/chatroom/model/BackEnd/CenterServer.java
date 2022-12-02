package com.example.chatroom.model.BackEnd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CenterServer extends Thread{

    //    单例模式，方便其他类快速访问
    private static CenterServer cs = new CenterServer();
    ServerSocket server = null;

    Socket clientSocket;
    private CenterServer() {}
    public CenterServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static CenterServer getCenterServer() {
        return cs;
    }

    //    用户账号集合Map<String id, User user>
    Map<String, User> clientMap = Collections.synchronizedMap(new HashMap<>());
    HashMap<String, ChatRoom> chatroomHashMap=new HashMap<>();//记录聊天室

    @Override
    public void run(){

        super.run();
        try{
            System.out.println("wait client connect...");
            clientSocket = server.accept();
            new sendMessThread().start();//连接并返回socket后，再启用发送消息线程
            System.out.println(clientSocket.getInetAddress().getHostAddress()+"SUCCESS TO CONNECT...");
            InputStream in = clientSocket.getInputStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len=in.read(buf))!=-1){
                System.out.println("client saying: "+new String(buf,0,len));
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    class sendMessThread extends Thread{
        @Override
        public void run(){
            super.run();
            Scanner scanner=null;
            OutputStream out = null;
            try{
                if(clientSocket != null){
                    InputStream inn = clientSocket.getInputStream();
                    out = clientSocket.getOutputStream();
                    int len = 0;
                    byte[] buf = new byte[1024];
                    while ((len=inn.read(buf))!=-1){
                        System.out.println("client saying: "+new String(buf,0,len));
                        out.write(buf,0,len);

                        out.flush();//清空缓存区的内容
                    }

                    //调试的时候通过读取键盘输入通信
//                    scanner = new Scanner(System.in);
//                    String in = "";
//                    do {
//                        in = scanner.next();
//                        out.write(("server saying: "+in).getBytes());
//                        out.flush();//清空缓存区的内容
//                    }while (!in.equals("q"));
//                    scanner.close();
                    try{
                        out.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //函数入口
    public static void main(String[] args) {
        CenterServer server = new CenterServer(8888);
        server.start();
    }
}
