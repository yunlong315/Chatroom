package com.example.chatroom;

import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.uiComponent.MemberBox;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomDetailViewController {
    @FXML
    private GridPane headGrid;
    @FXML
    private TextField changeNameTextField;
    @FXML
    private TextField inviteFriendTextField;
    private Stage stage;

    private final ChatModel chatModel = new ChatModel();
    private final Notifications notifications = new Notifications();
    private ChatRoom selectedChatRoom;
    private User nowUser;

    public RoomDetailViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONECHATROOM, this, this::update);
    }

    public void setSelectedChatRoom(ChatRoom selectedChatRoom) {
        this.selectedChatRoom = selectedChatRoom;
    }

    public void setNowUser(User nowUser) {
        this.nowUser = nowUser;
    }

    public void show(Scene scene) {
        stage = new Stage();
        stage.setTitle(String.format("%s(ID:%d)", selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
        stage.setScene(scene);
        showFriendsTable();
        stage.setTitle(String.format("%s(ID:%d)", selectedChatRoom.getChatroomName(), selectedChatRoom.getID()));
        stage.showAndWait();
    }

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

    @FXML
    public void onChangeRoomNameButtonClick() {
        String newRoomName = changeNameTextField.getText();
        if (!newRoomName.isEmpty()) {
            chatModel.changeRoomName(newRoomName, selectedChatRoom.getID());
            changeNameTextField.setText("");
        }
    }

    @FXML
    public void onInviteFriendButtonClick() {
        String friendAccount = inviteFriendTextField.getText();
        if (!friendAccount.isEmpty()) {
            chatModel.inviteFriend(nowUser.getUserAccount(), friendAccount, selectedChatRoom.getID());
            inviteFriendTextField.setText("");
        }
    }
}
