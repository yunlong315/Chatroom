package com.example.chatroom.uiComponent;

import com.example.chatroom.model.backend.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MemberBox extends VBox {
    private ImageView headImage;
    private Label nameLabel;

    public MemberBox(User user) {
        nameLabel = new Label(user.getUserName());
        headImage = new ImageView("file:" + user.getImagePath());
        headImage.setFitHeight(40);
        headImage.setFitWidth(40);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(headImage, nameLabel);
    }
}
