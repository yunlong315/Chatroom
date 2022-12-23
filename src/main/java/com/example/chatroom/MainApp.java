package com.example.chatroom;

import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.User;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainApp extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Chatroom");
        this.primaryStage.setOnCloseRequest((e) -> closeClient());
        showLoginRegisterView();
    }

    /**
     * 关闭客户端。
     */
    private void closeClient() {
        Client.closeClient();
    }

    /**
     * 显示登录注册界面。
     */
    public void showLoginRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("LoginRegisterView.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            LoginRegisterViewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示聊天界面。
     *
     * @param user-当前用户
     * @throws IOException-加载fxml时发生IO异常
     */
    public void showChatView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("ChatView.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        ChatViewController controller = loader.getController();
        controller.setMainApp(this);
        controller.setNowUser(user);
        controller.init();
        primaryStage.show();
    }
}
