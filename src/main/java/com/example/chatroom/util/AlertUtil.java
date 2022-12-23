package com.example.chatroom.util;

import javafx.scene.control.Alert;

/**
 * 弹窗工具。
 */
public class AlertUtil {
    /**
     * 弹窗显示信息。
     *
     * @param alertMessage-要显示的信息
     */
    public static void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }
}
