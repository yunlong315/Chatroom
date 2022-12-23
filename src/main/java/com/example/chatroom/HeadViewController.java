package com.example.chatroom;

import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.backend.User;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HeadViewController {
    @FXML
    private ImageView headImage;
    @FXML
    private Label nameLabel;
    private ChatModel chatModel;
    private final Notifications notifications = new Notifications();
    private Stage stage = new Stage();
    private User nowUser;

    public HeadViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONE_USER, this, this::update);
    }

    public void init() {
        headImage.setImage(new Image("file:" + nowUser.getImagePath()));
        nameLabel.setText(nowUser.getUserName());
    }

    public void setNowUser(User nowUser) {
        this.nowUser = nowUser;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public void update(String event) {
        User user = chatModel.getReceiveObject().getUser();
        headImage.setImage(new Image("file:" + user.getImagePath()));
    }

    @FXML
    public void onChangeHeadButtonClick() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择你的头像");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ALL Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File headFile = fileChooser.showOpenDialog(stage);
        if (headFile != null) {
            chatModel.changeUserHead(nowUser.getUserAccount(), headFile.getPath());
        }
    }

    public void show(Scene scene) {
        stage.setScene(scene);
        init();
        stage.show();
    }
}
