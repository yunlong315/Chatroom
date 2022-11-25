package com.example.chatroom;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatViewController {
    @FXML
    private Button chatButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button addButton;
    @FXML
    private Button emotionButton;
    @FXML
    private Button sendOutButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private Text friendNameText;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private ImageView headImage;

    @FXML
    private ListView chatListView;

    //切换listview的内容为聊天信息
    @FXML
    public void onChatButtonClick() {

    }

    //切换listview的内容为好友信息
    @FXML
    public void onFriendsButtonClick() {

    }

    //弹出设置对话框
    @FXML
    public void onSettingsButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage settingsStage = new Stage();
        settingsStage.setTitle("设置");
        settingsStage.setScene(scene);
        settingsStage.show();
    }

    //添加聊天室或者好友，取决于当前listview的状态
    @FXML
    public void onAddButtonClick() {

    }

    //弹出表情窗口
    @FXML
    public void onEmotionButtonClick() {

    }

    //发送消息
    @FXML
    public void onSendOutButtonClick() {
        String message = messageTextArea.getText();
        if (!message.isEmpty()) {

            messageTextArea.setText("");
        }
    }
}
