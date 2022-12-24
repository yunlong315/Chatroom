package com.example.chatroom;

import com.example.chatroom.backend.entity.User;
import com.example.chatroom.model.ChatModel;
import com.example.chatroom.model.Notifications;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * 头像界面的view层，控制头像界面的显示。通过订阅接受model层的发布。
 */
public class HeadViewController {
    private final Notifications notifications = new Notifications();
    @FXML
    private ImageView headImage;
    @FXML
    private Label nameLabel;
    private ChatModel chatModel;
    private final Stage stage = new Stage();
    private User nowUser;

    /**
     * 默认构造方法，构造时进行订阅
     */
    public HeadViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE_ONE_USER, this, this::update);
    }

    /**
     * 初始化界面
     */
    public void init() {
        headImage.setImage(new Image("file:" + nowUser.getImagePath()));
        nameLabel.setText(nowUser.getUserName());
    }

    /**
     * 设置界面对应的用户
     *
     * @param nowUser 界面对应的用户
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
     * 更新界面显示
     *
     * @param event 该方法订阅的事件
     */
    public void update(String event) {
        User user = chatModel.getReceiveObject().getUser();
        headImage.setImage(new Image("file:" + user.getImagePath()));
    }

    /**
     * 绑定“修改头像按钮”，点击按钮时能够选择一张图片更改头像
     *
     * @throws IOException
     */
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

    /**
     * 展示头像界面
     *
     * @param scene 头像界面
     */
    public void show(Scene scene) {
        stage.setScene(scene);
        init();
        stage.show();
    }
}
