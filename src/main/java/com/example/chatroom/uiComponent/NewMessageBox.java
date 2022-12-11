package com.example.chatroom.uiComponent;

import com.example.chatroom.model.backend.User;
import com.example.chatroom.util.AutoSizeUtil;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NewMessageBox {
    private HBox mainBox;
    private AnchorPane anchorPane;
    private VBox vBox;
    private Label name;
    private ImageView head;
    private TextArea messageText;

    public HBox left(User user, String message) {
        mainBox = new HBox();
        anchorPane = new AnchorPane();
        vBox = new VBox();
        name = new Label(user.getUserName());
        head = new ImageView();
        messageText = new TextArea(message);

        double autoWidth = AutoSizeUtil.getWidth(message);
        double autoHeight = AutoSizeUtil.getHeight(message);

        mainBox.setPrefSize(500, 60);
        vBox.setPrefSize(80, 60);
        anchorPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        head.setFitWidth(40);
        head.setFitHeight(40);
        messageText.setPrefSize(autoWidth, autoHeight < 50 ? autoHeight : 50);

        vBox.setAlignment(Pos.CENTER);
        mainBox.setAlignment(Pos.CENTER_LEFT);
        messageText.setEditable(false);
        name.setAlignment(Pos.CENTER);
        messageText.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        vBox.getChildren().addAll(name, head);
        anchorPane.getChildren().addAll(messageText);
        mainBox.getChildren().addAll(vBox, anchorPane);

        return mainBox;
    }

    public HBox right(User user, String message) {
        mainBox = new HBox();
        anchorPane = new AnchorPane();
        vBox = new VBox();
        name = new Label(user.getUserName());
        head = new ImageView();
        messageText = new TextArea(message);

        double autoWidth = AutoSizeUtil.getWidth(message);
        double autoHeight = AutoSizeUtil.getHeight(message);

        mainBox.setPrefSize(500, 60);
        vBox.setPrefSize(80, 60);
        anchorPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        head.setFitWidth(40);
        head.setFitHeight(40);
        messageText.setPrefSize(autoWidth, autoHeight < 50 ? autoHeight : 50);

        vBox.setAlignment(Pos.CENTER);
        mainBox.setAlignment(Pos.CENTER_RIGHT);
        messageText.setEditable(false);
        name.setAlignment(Pos.CENTER);
        messageText.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        vBox.getChildren().addAll(name, head);
        anchorPane.getChildren().addAll(messageText);
        mainBox.getChildren().addAll(anchorPane, vBox);

        return mainBox;
    }
}
