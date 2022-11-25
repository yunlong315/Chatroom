module com.example.chatroom {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chatroom to javafx.fxml;
    exports com.example.chatroom;
    exports com.example.chatroom.model;
    opens com.example.chatroom.model to javafx.fxml;
}