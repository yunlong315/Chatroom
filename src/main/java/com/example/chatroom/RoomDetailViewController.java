package com.example.chatroom;

import com.example.chatroom.backend.entity.ChatRoom;
import com.example.chatroom.backend.entity.User;
import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.uiComponent.MemberBox;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天室信息界面的view层，控制聊天室信息界面的显示。通过订阅接受model层的发布。
 */
public class RoomDetailViewController {
    private final Notifications notifications = new Notifications();
    @FXML
    private GridPane headGrid;
    @FXML
    private TextField changeNameTextField;
    @FXML
    private TextField inviteFriendTextField;
    private Stage stage;
    private ChatModel chatModel;
    private ChatRoom selectedChatRoom;
    private User nowUser;

    /**
     * 默认构造方法，构造时进行订阅
     */
    public RoomDetailViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONE_CHATROOM, this, this::update);
    }

    /**
     * 设置界面对应的聊天室
     *
     * @param selectedChatRoom 界面对应的聊天室
     */
    public void setSelectedChatRoom(ChatRoom selectedChatRoom) {
        this.selectedChatRoom = selectedChatRoom;
    }

    /**
     * 设置当前客户端用户
     *
     * @param nowUser 当前客户端用户
     */
    public void setNowUser(User nowUser) {
        this.nowUser = nowUser;
    }

    /**
     * 设置界面对应的Model
     *
     * @param chatModel 界面对应的Model
     */
    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 展示界面
     *
     * @param scene 界面
     */
    public void show(Scene scene) {
        stage = new Stage();
        stage.setTitle(String.format("%s(ID:%d)", selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
        stage.setScene(scene);
        showFriendsTable();
        stage.setTitle(String.format("%s(ID:%d)", selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
        stage.showAndWait();
    }

    /**
     * 更新当前界面
     *
     * @param event 该方法订阅的事件
     */
    public void update(String event) {

        ChatRoom chatRoom = chatModel.getReceiveObject().getChatRoom();
        if (chatRoom.equals(selectedChatRoom)) {
            selectedChatRoom = chatModel.getReceiveObject().getChatRoom();
            stage.setTitle(String.format("%s(ID:%d)", selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
            showFriendsTable();
        }
    }

    private void showFriendsTable() {
        Map<String, User> memberMap = selectedChatRoom.getUserHashMap();
        List<MemberBox> memberBoxes = new ArrayList<>();
        int rowIndex = 0;//行
        int colIndex = 0;//列
        for (User user : memberMap.values()) {
            memberBoxes.add(new MemberBox(user));
            System.out.println(user);
        }
        for (MemberBox box : memberBoxes) {
            headGrid.add(box, colIndex, rowIndex);
            colIndex++;
            rowIndex += colIndex / 3;
            colIndex %= 3;
        }
    }

    /**
     * 绑定“改名”按钮，点击后可更改聊天室名字
     */
    @FXML
    public void onChangeRoomNameButtonClick() {
        String newRoomName = changeNameTextField.getText();
        if (!newRoomName.isEmpty()) {
            chatModel.changeRoomName(newRoomName, selectedChatRoom.getID());
            changeNameTextField.setText("");
        }
    }

    /**
     * 绑定“邀请好友”按钮，点击后可邀请好友加入聊天室
     */
    @FXML
    public void onInviteFriendButtonClick() {
        String friendAccount = inviteFriendTextField.getText();
        if (!friendAccount.isEmpty()) {
            chatModel.inviteFriend(nowUser.getUserAccount(), friendAccount, selectedChatRoom.getID());
            inviteFriendTextField.setText("");
        }
    }
}
