package com.example.chatroom;

import com.example.chatroom.model.LoginRegisterModel;
import com.example.chatroom.model.Notifications;
import com.example.chatroom.model.backend.User;
import com.example.chatroom.util.AlertUtil;
import com.example.chatroom.util.PatternUtil;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginRegisterViewController {
    @FXML
    private TextField loginAccountField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField registerAccountField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerPasswordConfirmField;
    @FXML
    private TextField registerNameField;

    private MainApp mainApp;
    private final LoginRegisterModel loginRegisterModel = new LoginRegisterModel();
    private final Notifications notifications = new Notifications();
    private final BooleanProperty wasError = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    public LoginRegisterViewController() {
        notifications.subscribe(Notifications.EVENT_MODEL_UPDATE,
                this,
                this::update);
    }

    private void update(String event) {
        loginRegisterModel.getLoginRegisterObject().ifPresent(
                (resObject) -> {
                    wasError.set(resObject.getWasError());
                    if (!resObject.getWasError()) {
                        if (resObject.isLogin()) {
                            User user = resObject.getUser();
                            try {
                                mainApp.showChatView(user);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //登录成功，弹出成功信息
                            AlertUtil.showAlert("登录成功");
                        } else {
                            //注册成功，弹出成功信息
                            AlertUtil.showAlert("注册成功");
                        }
                    } else {
                        //弹出错误信息
                        AlertUtil.showAlert(resObject.getErrorMessage());
                    }
                }
        );
    }

    @FXML
    private void handleLogin() {
        if (isLoginInputValid()) {
            String userAccount = loginAccountField.getText();
            String passWord = loginPasswordField.getText();
            loginRegisterModel.login(userAccount, passWord);
        }
    }

    @FXML
    private void handleRegister() {
        if (isRegisterInputValid()) {
            String userAccount = registerAccountField.getText();
            String passWord = registerPasswordField.getText();
            String userName = registerNameField.getText();
            loginRegisterModel.register(userAccount, passWord,userName);
        }
    }

    private boolean isLoginInputValid() {
        String errorMessage = "";
        if (!loginAccountField.getText().matches(PatternUtil.getUserAccountPattern())) {
            errorMessage += "账号不合法\n";
        }
        if (!loginPasswordField.getText().matches(PatternUtil.getUserPasswordPattern())) {
            errorMessage += "密码不合法\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            AlertUtil.showAlert(errorMessage);
            return false;
        }
    }

    private boolean isRegisterInputValid() {
        String errorMessage = "";
        if (!registerAccountField.getText().matches(PatternUtil.getUserAccountPattern())) {
            errorMessage += "账号不合法\n";
        }
        if (!registerNameField.getText().matches(PatternUtil.getUserNamePattern())) {
            errorMessage += "用户名不合法\n";
        }
        if (!registerPasswordField.getText().matches(PatternUtil.getUserPasswordPattern())) {
            errorMessage += "密码不合法\n";
        }
        if (!registerPasswordField.getText().equals(registerPasswordConfirmField.getText())) {
            errorMessage += "密码不合法\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            AlertUtil.showAlert(errorMessage);
            return false;
        }
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
