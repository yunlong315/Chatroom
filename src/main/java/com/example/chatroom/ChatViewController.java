package com.example.chatroom;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
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
import java.util.List;

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
        chatModel.setUser(nowUser);
    }

    /**
     * 根据当前用户信息初始化界面
     */
    public void init() {
        chatListView.itemsProperty().bind(messagesProperty);
        String userName = nowUser.getUserName();
        //TODO:初始化用户姓名

        List<ChatRoom> chatRoomList = nowUser.getChatRoomList();
        //TODO:初始化聊天室列表

        List<User> userList = nowUser.getUserList();
        //TODO:（暂不考虑）初始化好友列表
    }



    public ChatViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_MESSAGE, this, this::updateMessageList);
    }

    /**
     * 更新聊天室列表。
     * @param event
     */
    public void updateChatList(String event) {
        //模拟更新数据
        chatModel.getChatObject().ifPresent(
            (chatObject)->
            {
                List<ChatRoom> chatRoomList = chatObject.getChatRoomList();
                //TODO:根据chatRoomList更新聊天室列表
            }
        );
    }

    /**
     * 更新好友列表。
     * @param event
     */
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

    /**
     * 更新消息列表。
     * @param event
     */
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

    /**
     * 绑定“聊天”按钮，切换listview的内容为聊天信息。
     */
    @FXML
    public void onChatButtonClick() {
        updateChatList("");
        chatListView.itemsProperty().bind(chatRoomsProperty);
        nowPage = Page.CHATPAGE;
    }

    /**
     * 绑定“好友”按钮，切换listview的内容为好友信息。
     */
    @FXML
    public void onFriendsButtonClick() {
        updateFriendsList("");
        chatListView.itemsProperty().bind(friendsProperty);
        nowPage = Page.FRIENDSPAGE;
    }

    /**
     * 绑定“设置”按钮，弹出设置对话框。
     * @throws IOException
     */
    @FXML
    public void onSettingsButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage settingsStage = new Stage();
        settingsStage.setTitle("设置");
        settingsStage.setScene(scene);
        settingsStage.show();
        onAddButtonClick();
    }
    /**
     * 绑定“添加”按钮。
     * 若当前视图为聊天列表，创建一个新聊天室；
     * 若当前视图为好友列表，添加新好友。
     */
    //
    @FXML
    public void onAddButtonClick() {
        String content = searchTextField.getText();
        switch (nowPage) {
            case CHATPAGE:
                chatModel.createChatroom();
                break;
            case FRIENDSPAGE:
                chatModel.addFriend(content);
                break;
        }
    }

    /**
     * 绑定“加入”按钮，加入指定id的聊天室。
     */
    @FXML
    public void onJoinButtonClick()
    {

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
