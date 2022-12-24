package com.example.chatroom.uiComponent;

import com.example.chatroom.backend.entity.ChatRoom;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * 继承自HBox的用于在聊天室列表显示单个聊天室的自定义UI组件
 */
public class ChatroomBox extends HBox {
    private final Label chatroomLabel;
    private ChatRoom chatRoom;

    /**
     * 根据传入的ChatRoom对象生成组件的构造方法
     *
     * @param room 要显示的聊天室
     */
    public ChatroomBox(ChatRoom room) {
        chatroomLabel = new Label(String.format("%s(ID:%d)", room.getChatroomName(), room.getID()));
        this.chatRoom = room;

        this.setPrefSize(240, 30);
        this.setAlignment(Pos.CENTER);
        chatroomLabel.setFont(new Font(14));
        chatroomLabel.setAlignment(Pos.CENTER);

        this.getChildren().add(chatroomLabel);

    }

    /**
     * 获得组件中的ChatRoom对象
     *
     * @return 组件中的ChatRoom对象
     */
    public ChatRoom getChatRoom() {
        return chatRoom;
    }


    /**
     * 根据传入的ChatRoom对象更新组件
     *
     * @param chatRoom 新的ChatRoom对象
     */
    public void update(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatroomLabel.setText(String.format("%s(ID:%d)", chatRoom.getChatroomName(), chatRoom.getID()));
    }
}
