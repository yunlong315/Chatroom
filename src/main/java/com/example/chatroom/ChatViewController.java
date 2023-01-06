package com.example.chatroom;

import com.example.chatroom.backend.entity.CachedData;
import com.example.chatroom.backend.entity.ChatRoom;
import com.example.chatroom.backend.entity.User;
import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.object.ChatObject;
import com.example.chatroom.object.ReceiveObject;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 聊天界面的view层，控制聊天界面的显示。通过订阅接受model层的发布。
 */
public class ChatViewController {
    private final ChatModel chatModel = new ChatModel();
    private final Notifications notifications = new Notifications();
    @FXML
    private Button chatButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button addButton;
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
    private Button headButton;
    @FXML
    private ListView<HBox> chatListView;
    @FXML
    private ListView<HBox> messageListView;
    @FXML
    private BorderPane chatBorderPane;
    //当前所在界面
    private Page nowPage;
    private MainApp mainApp;
    private User nowUser;
    private ChatRoom selectedChatRoom = null;
    private ChatroomBox selectedChatRoomBox = null;
    private final ReadOnlyObjectProperty<ObservableList<HBox>> chatRoomsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ReadOnlyObjectProperty<ObservableList<HBox>> friendsProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ReadOnlyObjectProperty<ObservableList<HBox>> messagesProperty =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());

    /**
     * 默认构造函数，构造时进行订阅
     */
    public ChatViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ChatList, this, this::updateChatList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_FriendsList, this, this::updateFriendsList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_MESSAGE, this, this::updateMessageList);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_SENDED, this, this::updateSended);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONE_CHATROOM, this, this::updateOneChatroom);
        notifications.subscribe(Notifications.EVENT_MODEL_OPERATION_DONE, this, this::checkStatus);
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONE_USER, this, this::updateOneUser);
    }

    /**
     * 传入主程序类。
     *
     * @param mainApp-主程序
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * 传入当前用户。
     *
     * @param nowUser-当前用户
     */
    public void setNowUser(User nowUser) {
        CachedData.addUser(nowUser);
        this.nowUser = nowUser;
        chatModel.setUser(nowUser);
    }


    /**
     * 切换到选中聊天室的聊天界面。
     *
     * @param chatRoom-选中的聊天室。
     */
    private void changeToOneChatroom(ChatRoom chatRoom) {
        titleText.setText(chatRoom.getChatroomName() + String.format("(ID:%d)", chatRoom.getID()));
        List<HBox> messageList = new MessageBox().getBoxList(nowUser, CachedData.getMessageList(chatRoom.getID()));
        messagesProperty.get().clear();
        messagesProperty.get().addAll(messageList);
    }

    /**
     * 给聊天室列表对应的listview加上监听，
     * 使能够根据选择的聊天室切换聊天界面
     */
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

    /**
     * 初始化聊天室列表。
     */
    private void initChatRoomList() {
        for (ChatRoom chatRoom : nowUser.getChatRoomList()) {
            CachedData.addChatRoom(chatRoom);
            chatRoomsProperty.get().addAll(new ChatroomBox(chatRoom));
        }
    }

    /**
     * 初始化好友列表。
     */
    private void initFriendList() {
        for (User user : nowUser.getFriendsList()) {
            friendsProperty.get().addAll(new FriendBox(user));
        }
    }

    /**
     * 初始化界面。
     */
    public void init() {
        messageListView.itemsProperty().bind(messagesProperty);
        addChatListViewListener();
        userNameText.setText(nowUser.getUserName());
        userAccountText.setText(nowUser.getUserAccount());

        initChatRoomList();
        initFriendList();

        //设置头像
        ImageView imageView = new ImageView(new Image("file:" + nowUser.getImagePath()));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        headButton.setGraphic(imageView);

        nowPage = Page.CHATPAGE;

        chatListView.itemsProperty().bind(chatRoomsProperty);//打开聊天界面时，默认显示聊天列表
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
     * 更新一个聊天室信息，刷新该聊天室在界面中的所有相关显示。
     *
     * @param event 该方法订阅的事件。
     */
    public void updateOneChatroom(String event) {
        ChatRoom chatRoom = chatModel.getReceiveObject().getChatRoom();
        if (chatRoom == null) {
            System.out.println("view层接收到chatroom为空");
            return;
        } else {
            System.out.println("view层接收到" + chatRoom.getID() + "号聊天室有更新");
        }
        for (HBox box : chatRoomsProperty.get()) {
            ChatroomBox chatroomBox = (ChatroomBox) box;
            if (chatroomBox.getChatRoom().equals(chatRoom)) {
                chatroomBox.update(chatRoom);
                if (selectedChatRoom != null && selectedChatRoom.equals(chatRoom)) {
                    selectedChatRoom = chatRoom;
                    changeToOneChatroom(chatRoom);
                }
                break;
            }
        }
    }

    /**
     * 更新一个用户的信息，刷新该用户在界面中的所有相关显示。
     *
     * @param event-该方法订阅的事件。
     */
    public void updateOneUser(String event) {
        User user = chatModel.getReceiveObject().getUser();
        if (user.equals(nowUser)) {
            nowUser = user;
            ImageView headImage = new ImageView("file:" + nowUser.getImagePath());
            headImage.setFitWidth(50);
            headImage.setFitHeight(50);
            headButton.setGraphic(headImage);
        } else {
            for (HBox box : friendsProperty.get()) {
                FriendBox friendBox = (FriendBox) box;
                if (friendBox.getFriend().equals(user)) {
                    friendBox.update(user);
                    break;
                }
            }
        }
    }

    /**
     * 更新发送信息的状态。如发送失败则显示失败信息，发送成功则无需操作。
     *
     * @param event-该方法订阅的事件
     */
    private void updateSended(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (chatObject.wasError()) {
                        AlertUtil.showAlert(chatObject.getErrorMessage());
                    }
                }
        );
    }

    /**
     * 新增聊天室，更新聊天室列表。
     *
     * @param event-该方法订阅的事件
     */
    public void updateChatList(String event) {
        chatModel.getChatObject().ifPresent(
                (chatObject) -> {
                    if (!chatObject.wasError()) {
                        ChatRoom chatroom = chatObject.getChatRoom();
                        CachedData.addChatRoom(chatroom);
                        chatroom = CachedData.getChatroom(chatroom.getID());
                        chatRoomsProperty.get().addAll(new ChatroomBox(chatroom));

                    } else {
                        AlertUtil.showAlert(chatObject.getErrorMessage());
                    }
                }
        );
    }

    /**
     * 更新好友列表。
     *
     * @param event-该方法订阅的事件。
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
     * @param event-该方法事件。
     */
    public void updateMessageList(String event) {
        ReceiveObject receiveObject = chatModel.getReceiveObject();
        User user = CachedData.getUser(receiveObject.getSender());
        HBox messageBox;
        if (selectedChatRoom != null && selectedChatRoom.equals(receiveObject.getChatRoom())) {
//            if (user.equals(nowUser)) {
//                messageBox = new MessageBox().right(user, receiveObject.getMessage());
//            } else {
//                messageBox = new MessageBox().left(user, receiveObject.getMessage());
//            }
//            messagesProperty.get().addAll(messageBox);
            changeToOneChatroom(selectedChatRoom);
        }
    }

    /**
     * 绑定“聊天”按钮，切换listview的内容为聊天信息。
     */
    @FXML
    public void onChatButtonClick() {
        chatListView.itemsProperty().bind(chatRoomsProperty);
        searchTextField.setVisible(false);
        chatBorderPane.setVisible(true);
        addButton.setText("创建聊天室");
        nowPage = Page.CHATPAGE;
    }

    /**
     * 绑定“好友”按钮，切换l列表内容为好友。
     **/
    @FXML
    public void onFriendsButtonClick() {
        chatListView.itemsProperty().bind(friendsProperty);
        searchTextField.setVisible(true);
        chatBorderPane.setVisible(false);
        addButton.setText("添加好友");
        nowPage = Page.FRIENDSPAGE;
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
                searchTextField.setText("");
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

    /**
     * 绑定“发送”按钮，发送消息。
     */
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

    /**
     * 绑定“聊天室信息”按钮，点击按钮能够展示相应聊天室的信息界面
     *
     * @throws IOException
     */
    @FXML
    public void onDetailButtonClick() throws IOException {
        if (selectedChatRoom != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatroomDetailView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            RoomDetailViewController controller = fxmlLoader.getController();
            controller.setNowUser(nowUser);
            controller.setSelectedChatRoom(selectedChatRoom);
            controller.setChatModel(chatModel);
            controller.show(scene);
        }
    }

    /**
     * 绑定当前用户头像。显示用户详细信息。
     *
     * @throws IOException
     */
    @FXML
    public void onHeadButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HeadView.fxml"));
        Scene scene = new Scene(loader.load());
        HeadViewController controller = loader.getController();
        controller.setChatModel(chatModel);
        controller.setNowUser(nowUser);
        controller.show(scene);
        System.out.println(nowUser.getImagePath());
        System.out.println(nowUser.isHasImage());
    }

    private enum Page {
        CHATPAGE,
        FRIENDSPAGE
    }
}
