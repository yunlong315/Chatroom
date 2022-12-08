package com.example.chatroom.uiComponent;

import com.example.chatroom.model.backend.User;
import com.example.chatroom.util.AutoSizeUtil;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class MessageBox {
    private Pane pane;

    private Pane head;              // 头像
    private Label nikeName;         // 昵称区
    private Label infoContentArrow; // 内容箭头
    private TextArea infoContent;   // 内容

    public Pane left(User user, String message) {

        String userName = user.getUserName();
        //String userHead=user.getHead();

        double autoHeight = AutoSizeUtil.getHeight(message);
        double autoWidth = AutoSizeUtil.getWidth(message);

        pane = new Pane();
        pane.setPrefSize(500, 50 + autoHeight);
        ObservableList<Node> children = pane.getChildren();

        // 头像
        head = new Pane();
        head.setPrefSize(50, 50);
        head.setLayoutX(15);
        head.setLayoutY(15);
        //设置头像
        //head.setStyle(String.format("-fx-background-image: url('/fxml/chat/img/head/%s.png')", userHead));
        children.add(head);

        // 昵称
        nikeName = new Label();
        nikeName.setPrefSize(450, 20);
        nikeName.setLayoutX(75);
        nikeName.setLayoutY(5);
        nikeName.setText(userName);
        children.add(nikeName);

//        // 箭头
//        infoContentArrow = new Label();
//        infoContentArrow.setPrefSize(5, 20);
//        infoContentArrow.setLayoutX(75);
//        infoContentArrow.setLayoutY(30);
//        infoContentArrow.getStyleClass().add("box_infoContent_arrow");
//        children.add(infoContentArrow);

        // 内容
        infoContent = new TextArea();
        infoContent.setPrefWidth(autoWidth);
        infoContent.setPrefHeight(autoHeight);
        infoContent.setLayoutX(80);
        infoContent.setLayoutY(30);
        infoContent.setWrapText(true);
        infoContent.setEditable(false);
        infoContent.setText(message);
        children.add(infoContent);

        return pane;
    }

    public Pane right(User user, String message) {

        String userName = user.getUserName();
        //String userHead=user.getHead();

        double autoHeight = AutoSizeUtil.getHeight(message);
        double autoWidth = AutoSizeUtil.getWidth(message);

        pane = new Pane();
        pane.setPrefSize(500, 50 + autoHeight);
        pane.setLayoutX(853);
        pane.setLayoutY(0);
        ObservableList<Node> children = pane.getChildren();

        // 头像
        head = new Pane();
        head.setPrefSize(50, 50);
        head.setLayoutX(770);
        head.setLayoutY(15);
        //设置头像
        //head.setStyle(String.format("-fx-background-image: url('/fxml/chat/img/head/%s.png')", userHead));
        children.add(head);


        // 昵称
        nikeName = new Label();
        nikeName.setPrefSize(450, 20);
        nikeName.setLayoutX(75);
        nikeName.setLayoutY(5);
        nikeName.setText(userName);
        children.add(nikeName);

//        // 箭头
//        infoContentArrow = new Label();
//        infoContentArrow.setPrefSize(5, 20);
//        infoContentArrow.setLayoutX(755);
//        infoContentArrow.setLayoutY(15);
//        infoContentArrow.getStyleClass().add("box_infoContent_arrow");
//        children.add(infoContentArrow);

        // 内容：文字
        infoContent = new TextArea();
        infoContent.setPrefWidth(autoWidth);
        infoContent.setPrefHeight(autoHeight);
        infoContent.setLayoutX(755 - autoWidth);
        infoContent.setLayoutY(15);
        infoContent.setWrapText(true);
        infoContent.setEditable(false);
        infoContent.setText(message);
        infoContent.getStyleClass().add("box_infoContent_right");
        children.add(infoContent);

        return pane;
    }
}
