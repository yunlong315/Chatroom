package com.example.chatroom;

import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.ChatObject;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.util.AlertUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
    private Text titleText;
    @FXML
    private Text userNameText;
    @FXML
    private Text userAccountText;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private ImageView headImage;

    @FXML
    private ListView chatListView;

    @FXML
    private ListView<HBox> messageListView;

    //当前所在界面
    private Page nowPage;

    private enum Page {
        CHATPAGE,
        FRIENDSPAGE
    }

    private MainApp mainApp;
    private User nowUser;

    private ChatRoom selectedChatRoom = null;
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
     * 实现根据选定的聊天室切换聊天界面
     */
    private void changeToOneChatroom(ChatRoom chatRoom) {
        titleText.setText("聊天室" + chatRoom.getID());
        //todo:获得当前聊天室的所有聊天记录
        List<HBox> messageList = new ArrayList<>();
        messagesProperty.get().addAll(messageList);
    }

    public void addChatListViewListener() {
        chatListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue instanceof ChatRoom) {
                        ChatRoom chatRoom = (ChatRoom) newValue;
                        selectedChatRoom = chatRoom;
                        changeToOneChatroom(chatRoom);
                    }
                }
        );
    }

    /**
     * 根据当前用户信息初始化界面
     */
    public void init() {
        messageListView.itemsProperty().bind(messagesProperty);
        addChatListViewListener();
        userNameText.setText(nowUser.getUserName());
        userAccountText.setText(nowUser.getUserAccount());
        chatRoomsProperty.get().addAll(nowUser.getChatRoomList());
        friendsProperty.get().addAll(nowUser.getFriendsList());
        nowPage = Page.CHATPAGE;
        chatListView.itemsProperty().bind(chatRoomsProperty);//打开聊天界面时，默认显示聊天列表
    }

    public ChatViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_MESSAGE, this, this::updateMessageList);
    }

    /**
     * 更新聊天室列表。
     *
     * @param event
     */
    public void updateChatList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if(!chatObject.wasError()) {
                        chatRoomsProperty.get().addAll(chatObject.getChatRoom());
                    }
                    else
                    {
                        AlertUtil.showAlert(chatObject.getErrorMessage());
                    }
                }
        );
    }

    /**
     * 更新好友列表。
     *
     * @param event
     */
    public void updateFriendsList(String event) {
        //模拟更新数据
        friendsProperty.get().addAll(
                new User("zzz"),
                new User("xxx"));
    }

    private HBox getMessageBox(ChatObject chatObject) {
        HBox retBox = new HBox();
        Text messageText = new Text(chatObject.getMessage());
        //TODO:用户发送消息时，自己的屏幕上就显示发送的消息。服务器不会将消息转发给自己。
//        if (chatObject.getSender().equals(nowUser)) {
//            retBox.setAlignment(Pos.CENTER_RIGHT);
//        } else {
//            retBox.setAlignment(Pos.CENTER_LEFT);
//        }
        retBox.setAlignment(Pos.CENTER_LEFT);
        retBox.getChildren().addAll(messageText);
        return retBox;
    }

    /**
     * 更新消息列表。
     *
     * @param event
     */
    public void updateMessageList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (chatObject.wasError()) {
                        //弹出错误信息
                        String errorMessage = chatObject.getErrorMessage();
                        AlertUtil.showAlert(errorMessage);
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
     *
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
    public void onJoinButtonClick() {
        TextInputDialog textInputDialog = new TextInputDialog("");
        textInputDialog.setTitle("加入聊天室");
        textInputDialog.setHeaderText("请输入聊天室ID");
        textInputDialog.setContentText("聊天室ID:");
        textInputDialog.showAndWait();
        String ID;
        int id;
        try {
            ID = textInputDialog.getResult();
        } catch (final NoSuchElementException ex) {
            ID = null;
        }
        if (ID != null) {
            try {
                id = Integer.parseInt(ID);
                chatModel.joinChatroom(id);
            } catch (NumberFormatException e) {
                AlertUtil.showAlert("输入的ID非数字");
            }
        }
    }

    //弹出表情窗口
    @FXML
    public void onEmotionButtonClick() {

    }

    //发送消息
    @FXML
    public void onSendOutButtonClick() {
        //todo:从chatlistview中获得当前聊天室的相关信息

        String message = messageTextArea.getText();
        if (!message.isEmpty()) {
            //发送非空字符串
            chatModel.sendMessage(message, selectedChatRoom.getID());
        }
    }
}
