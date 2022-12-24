package com.example.chatroom.uiComponent;

import com.example.chatroom.backend.entity.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * 继承自VBox的用于在聊天室的成员列表中显示单个成员的自定义UI组件
 */
public class MemberBox extends VBox {
    private final ImageView headImage;
    private final Label nameLabel;

    /**
     * 根据传入的User对象生成组件的构造方法
     *
     * @param user 要显示的成员
     */
    public MemberBox(User user) {
        nameLabel = new Label(user.getUserName());
        headImage = new ImageView("file:" + user.getImagePath());
        headImage.setFitHeight(40);
        headImage.setFitWidth(40);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(headImage, nameLabel);
    }
}
