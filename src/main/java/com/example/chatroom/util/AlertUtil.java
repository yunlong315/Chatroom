package com.example.chatroom.util;

import javafx.scene.control.Alert;

public class AlertUtil {
    /**
     * 弹窗显示信息。
     *
     * @param alertMessage
     */
    public static void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }
}
