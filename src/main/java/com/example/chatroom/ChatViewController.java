package com.example.chatroom;

import com.example.chatroom.model.BackEnd.ChatRoom;
import com.example.chatroom.model.BackEnd.User;
import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.ChatObject;
import com.example.chatroom.model.Notifications;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

    @FXML
    private ListView<HBox> messageListView;

    //当前所在界面
    private Page nowPage = Page.CHATPAGE;

    private enum Page {
        CHATPAGE,
        FRIENDSPAGE
    }

    private MainApp mainApp;
    private User nowUser;
    private final ChatModel chatModel = new ChatModel();
    private final Notifications notifications = new Notifications();
    private ReadOnlyObjectProperty<ObservableList<ChatRoom>> chatRoomsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ReadOnlyObjectProperty<ObservableList<User>> friendsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ReadOnlyObjectProperty<ObservableList<HBox>> messagesProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setNowUser(User nowUser) {
        this.nowUser = nowUser;
    }

    //在显示页面前初始化当前用户、聊天室、好友等信息
    public void init() {
        //todo
    }

    public ChatViewController() {
        chatListView.itemsProperty().bind(messagesProperty);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_MESSAGE, this, this::updateMessageList);
    }

    public void updateChatList(String event) {
        //模拟更新数据

    }

    public void updateFriendsList(String event) {
        //模拟更新数据
        friendsProperty.get().addAll(
                new User("zzz"),
                new User("xxx"));
    }

    public HBox getMessageBox(ChatObject chatObject) {
        HBox retBox = new HBox();
        Text messageText = new Text(chatObject.getMessage());
        if (chatObject.getSender().equals(nowUser)) {
            retBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            retBox.setAlignment(Pos.CENTER_LEFT);
        }
        retBox.getChildren().addAll(messageText);
        return retBox;
    }


    public void updateMessageList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (chatObject.isWasError()) {
                        //弹出错误信息
                        String errorMessage = chatObject.getErrorMessage();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(errorMessage);
                        alert.showAndWait();
                    } else {
                        HBox messageHBox = getMessageBox(chatObject);
                        messagesProperty.get().addAll(messageHBox);
                    }
                });
    }

    //切换listview的内容为聊天信息
    @FXML
    public void onChatButtonClick() {
        updateChatList("");
        chatListView.itemsProperty().bind(chatRoomsProperty);
        nowPage = Page.CHATPAGE;
    }

    //切换listview的内容为好友信息
    @FXML
    public void onFriendsButtonClick() {
        updateFriendsList("");
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
            //发送非空字符串
            chatModel.sendMessage(message);
        }
    }
}
