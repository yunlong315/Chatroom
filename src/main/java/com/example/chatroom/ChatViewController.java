package com.example.chatroom;

import com.example.chatroom.model.BackEnd.ChatRoom;
import com.example.chatroom.model.BackEnd.User;
import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    //当前所在界面
    private Page nowPage = Page.CHATPAGE;

    private enum Page {
        CHATPAGE,
        FRIENDSPAGE
    }

    private MainApp mainApp;
    private final ChatModel chatModel = new ChatModel();
    private final Notifications notifications = new Notifications();
    private ReadOnlyObjectProperty<ObservableList<ChatRoom>> chatRoomsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ReadOnlyObjectProperty<ObservableList<User>> friendsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public ChatViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
    }

    public void updateChatList(String event) {
        //模拟更新数据
//        chatRoomsProperty.get().addAll(
//                new ChatRoom("1", "聊天室1"),
//                new ChatRoom("2", "聊天室2"));
    }

    public void updateFriendsList(String event) {
        //模拟更新数据
        friendsProperty.get().addAll(
                new User("zzz"),
                new User("xxx"));
    }

    //切换listview的内容为聊天信息
    @FXML
    public void onChatButtonClick() {
        chatListView.itemsProperty().bind(chatRoomsProperty);
        nowPage = Page.CHATPAGE;
    }

    //切换listview的内容为好友信息
    @FXML
    public void onFriendsButtonClick() {
        chatListView.itemsProperty().bind(friendsProperty);
        nowPage = Page.FRIENDSPAGE;
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
        String content = searchTextField.getText();
        switch (nowPage) {
            case CHATPAGE:
                chatModel.addChatRoom();
                break;
            case FRIENDSPAGE:
                chatModel.addFriend(content);
                break;
        }
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
            chatModel.sendMessage(message);
            messageTextArea.setText("");
        }
    }
}
