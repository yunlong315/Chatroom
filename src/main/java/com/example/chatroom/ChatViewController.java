package com.example.chatroom;

import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.ChatObject;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.ReceiveObject;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.uiComponent.ChatroomBox;
import com.example.chatroom.uiComponent.FriendBox;
import com.example.chatroom.uiComponent.MessageBox;
import com.example.chatroom.util.AlertUtil;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    private Button detailButton;
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
    private Button headButton;

    @FXML
    private ListView<HBox> chatListView;

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
    private ChatroomBox selectedChatRoomBox = null;

    private final ChatModel chatModel = new ChatModel();
    private final Notifications notifications = new Notifications();
    private ReadOnlyObjectProperty<ObservableList<HBox>> chatRoomsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ReadOnlyObjectProperty<ObservableList<HBox>> friendsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ReadOnlyObjectProperty<ObservableList<HBox>> messagesProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setNowUser(User nowUser) {
        CachedData.addUser(nowUser);
        this.nowUser = nowUser;
        chatModel.setUser(nowUser);
    }

    /**
     * 实现根据选定的聊天室切换聊天界面
     */
    private void changeToOneChatroom(ChatRoom chatRoom) {
        titleText.setText(chatRoom.getChatroomName() + String.format("(ID:%d)", chatRoom.getID()));
        List<HBox> messageList = new MessageBox().getBoxList(nowUser, CachedData.getMessageList(chatRoom.getID()));

        messagesProperty.get().clear();
        messagesProperty.get().addAll(messageList);
    }

    public void addChatListViewListener() {
        chatListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue instanceof ChatroomBox) {
                        ChatRoom chatRoom = ((ChatroomBox) newValue).getChatRoom();
                        selectedChatRoom = chatRoom;
                        selectedChatRoomBox = (ChatroomBox) newValue;
                        changeToOneChatroom(chatRoom);
                    }
                }
        );
    }


    private void initChatRoomList() {
        for (ChatRoom chatRoom : nowUser.getChatRoomList()) {
            CachedData.addChatRoom(chatRoom);
            chatRoomsProperty.get().addAll(new ChatroomBox(chatRoom));
        }
    }

    private void initFriendList() {
        for (User user : nowUser.getFriendsList()) {
            friendsProperty.get().addAll(new FriendBox(user));
        }
    }

    /**
     * 根据当前用户信息初始化界面
     */
    public void init() {
        messageListView.itemsProperty().bind(messagesProperty);
        addChatListViewListener();
        userNameText.setText(nowUser.getUserName());
        userAccountText.setText(nowUser.getUserAccount());
        /**
         * 测试图片显示
         */
//        Image image = new Image("C:\\Users\\86155\\Desktop\\mkm.jpg");
//        headImage.setImage(image);
        initChatRoomList();
        initFriendList();

        nowPage = Page.CHATPAGE;

        chatListView.itemsProperty().bind(chatRoomsProperty);//打开聊天界面时，默认显示聊天列表
    }

    public ChatViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_MESSAGE, this, this::updateMessageList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_SENDED, this, this::updateSended);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONECHATROOM, this, this::updateOneChatroom);
        notifications.subscribe(Notifications.EVENT_MODEL_OPERATION_DONE, this, this::checkStatus);
    }

    /**
     * 检查操作是否成功。不成功则弹窗。
     *
     * @param event
     */
    private void checkStatus(String event) {
        ChatObject chatObject = chatModel.getChatObject().get();
        if (chatObject.wasError()) {
            String errorMessage = chatObject.getErrorMessage();
            AlertUtil.showAlert(errorMessage);
        }
    }

    /**
     * 更新一个聊天室
     *
     * @param event
     */
    public void updateOneChatroom(String event) {
        ChatRoom chatRoom = chatModel.getReceiveObject().getChatRoom();
        //fixme:此处chatRoom会为空
        if (chatRoom == null) {
            System.out.println("传入chatroom为空");
            return;
        } else {
            System.out.println(chatRoom.getID() + "号聊天室有更新");
        }
        CachedData.addChatRoom(chatRoom);
        for (HBox box : chatRoomsProperty.get()) {
            ChatroomBox chatroomBox = (ChatroomBox) box;
            if (chatroomBox.getChatRoom().equals(chatRoom)) {
                chatroomBox.update(chatRoom);
                break;
            }
        }
    }

    /**
     * 更新发送信息的状态
     *
     * @param event
     */
    private void updateSended(String event) {
        //如发送失败则显示失败信息，发送成功则无需操作。
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (chatObject.wasError()) {
                        AlertUtil.showAlert(chatObject.getErrorMessage());
                    }
                }
        );
    }

    /**
     * 更新聊天室列表。
     *
     * @param event
     */
    public void updateChatList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (!chatObject.wasError()) {
                        chatRoomsProperty.get().addAll(new ChatroomBox(chatObject.getChatRoom()));
                        ChatRoom chatroom = chatObject.getChatRoom();
                        CachedData.addChatRoom(chatroom);
                    } else {
                        AlertUtil.showAlert(chatObject.getErrorMessage());
                    }
                }
        );
    }

    /**
     * 更新好友列表,从chatObject中获取新好友。
     *
     * @param event
     */
    public void updateFriendsList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (!chatObject.wasError()) {
                        User user = chatObject.getUser();
                        CachedData.addUser(user);
                        user = CachedData.getUser(user.getUserAccount());
                        nowUser.addFriend(user);
                        friendsProperty.get().clear();
                        List<User> friends = nowUser.getFriendsList();
                        for (User friend : friends) {
                            friendsProperty.get().add(new FriendBox(friend));
                        }
                    } else {
                        String errorMessage = chatObject.getErrorMessage();
                        AlertUtil.showAlert(errorMessage);
                    }

                }
        );
    }


    /**
     * 更新消息列表。
     *
     * @param event
     */
    public void updateMessageList(String event) {
        ReceiveObject receiveObject = chatModel.getReceiveObject();
        User user = CachedData.getUser(receiveObject.getSender());
        HBox messageBox = new MessageBox().left(user, receiveObject.getMessage());
        messagesProperty.get().addAll(messageBox);
    }

    /**
     * 绑定“聊天”按钮，切换listview的内容为聊天信息。
     */
    @FXML
    public void onChatButtonClick() {
        chatListView.itemsProperty().bind(chatRoomsProperty);
        searchTextField.setDisable(true);
        addButton.setText("创建新的聊天室");
        nowPage = Page.CHATPAGE;
    }

    /**
     * 绑定“好友”按钮，切换listview的内容为好友信息。
     */
    @FXML
    public void onFriendsButtonClick() {
        chatListView.itemsProperty().bind(friendsProperty);
        searchTextField.setDisable(false);
        addButton.setText("添加好友");
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
        settingsStage.showAndWait();
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
                if (!content.isEmpty())
                    chatModel.addFriend(nowUser.getUserAccount(), content);
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
        if (selectedChatRoom == null) {
            AlertUtil.showAlert("请先选择一个聊天室！");
        } else {
            String message = messageTextArea.getText();
            if (!message.isEmpty()) {
                //发送非空字符串
                chatModel.sendMessage(message, selectedChatRoom.getID());
                HBox myMessageBox = new MessageBox().right(nowUser, message);
                messagesProperty.get().addAll(myMessageBox);
            }
        }
        messageTextArea.setText("");
    }

    @FXML
    public void oldonChangeRoomNameButtonClick() {
        //todo:后续要通过后端操作
        if (selectedChatRoom == null) {
            return;
        }
        TextInputDialog textInputDialog = new TextInputDialog("");
        textInputDialog.setTitle("修改聊天室名称");
        textInputDialog.setHeaderText("请输入新的聊天室名称");
        textInputDialog.setContentText("聊天室名称:");
        textInputDialog.showAndWait();
        String name;
        try {
            name = textInputDialog.getResult();
        } catch (final NoSuchElementException ex) {
            name = null;
        }
        if (name != null) {
            selectedChatRoom.setChatroomName(name);
            selectedChatRoomBox.setChatroomLabel();
            titleText.setText(String.format("%s(ID:%d)",
                    selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
        }
    }


    @FXML
    public void onDetailButtonClick() throws IOException {
        if (selectedChatRoom != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatroomDetailView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            RoomDetailViewController controller = fxmlLoader.getController();
            controller.setNowUser(nowUser);
            controller.setSelectedChatRoom(selectedChatRoom);
            //todo
            for (User user : selectedChatRoom.getUserHashMap().values()) {
                System.out.println(user);
            }
            controller.show(scene);
        }
    }

    @FXML
    public void onHeadButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HeadView.fxml"));
        Scene scene = new Scene(loader.load());
        HeadViewController controller = loader.getController();

    }
}
