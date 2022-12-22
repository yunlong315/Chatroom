package com.example.chatroom.uiComponent;

import com.example.chatroom.model.backend.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class FriendBox extends HBox {
    private Label friendLabel;
    private ImageView head;
    private User friend;

    public FriendBox(User friend) {
        friendLabel = new Label(String.format("%s(ID:%s)", friend.getUserName(), friend.getUserAccount()));
        head = new ImageView(friend.getImagePath());
        this.friend = friend;

        this.setPrefSize(240, 50);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(0, 0, 0, 10));
        this.setSpacing(10);

        head.setFitHeight(45);
        head.setFitWidth(45);

        friendLabel.setFont(new Font(14));
        friendLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(head, friendLabel);

    }

    public User getFriend() {
        return friend;
    }

    public void update(User user) {
        friend = user;
        friendLabel.setText(String.format("%s(ID:%s)", friend.getUserName(), friend.getUserAccount()));
        head.setImage(new Image(friend.getImagePath()));
    }
}
