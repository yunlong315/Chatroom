package com.example.chatroom.uiComponent;

import com.example.chatroom.backend.entity.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * 继承自HBox的用于在好友列表显示单个好友的自定义UI组件
 */
public class FriendBox extends HBox {
    private final Label friendLabel;
    private final ImageView head;
    private User friend;

    /**
     * 根据传入的User对象生成组件的构造方法
     *
     * @param friend 要显示的好友
     */
    public FriendBox(User friend) {
        friendLabel = new Label(String.format("%s(ID:%s)", friend.getUserName(), friend.getUserAccount()));
        head = new ImageView("file:" + friend.getImagePath());
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

    /**
     * 获得组件中的User对象
     *
     * @return 组件中的User对象
     */
    public User getFriend() {
        return friend;
    }

    /**
     * 根据传入的User对象更新组件
     *
     * @param user 新的User对象
     */
    public void update(User user) {
        friend = user;
        friendLabel.setText(String.format("%s(ID:%s)", friend.getUserName(), friend.getUserAccount()));
        head.setImage(new Image("file:" + friend.getImagePath()));
    }
}
