package com.example.chatroom.uiComponent;

import com.example.chatroom.model.backend.ChatRoom;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ChatroomBox extends HBox {
    private Label chatroomLabel;
    private ChatRoom chatRoom;

    public ChatroomBox(ChatRoom room) {
        chatroomLabel = new Label(String.format("%s(ID:%d)", room.getChatroomName(), room.getID()));
        this.chatRoom = room;

        this.setPrefSize(240, 30);
        this.setAlignment(Pos.CENTER);
        chatroomLabel.setFont(new Font(14));
        chatroomLabel.setAlignment(Pos.CENTER);

        this.getChildren().add(chatroomLabel);

    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatroomLabel(){
        chatroomLabel.setText(String.format("%s(ID:%d)", chatRoom.getChatroomName(), chatRoom.getID()));
    }
}
