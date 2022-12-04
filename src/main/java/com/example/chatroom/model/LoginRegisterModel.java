package com.example.chatroom.model;

import com.example.chatroom.model.backend.Client;
import com.example.chatroom.model.backend.reponses.LoginResponse;
import com.example.chatroom.model.backend.reponses.RegisterResponse;

import java.util.Optional;

public class LoginRegisterModel {
    private final Notifications notifications = new Notifications();
    //private final Client client;
    private Optional<LoginRegisterObject> loginRegisterObject = Optional.empty();

    public LoginRegisterModel() {
        Client.getClient().init();
    }

    public Optional<LoginRegisterObject> getLoginRegisterObject() {
        return loginRegisterObject;
    }

    public Optional<LoginRegisterObject> login(String userAccount, String passWord) {
        try {
            Client.getClient().login(userAccount, passWord);
            Thread.sleep(100);
            LoginResponse loginResponse = Client.getClient().loginResponse();
            LoginRegisterObject lro = new LoginRegisterObject(loginResponse);
            loginRegisterObject = Optional.of(lro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }

    public Optional<LoginRegisterObject> register(String userAccount, String passWord, String userName) {
        try {
            Client.getClient().register(userAccount, passWord, userName);
            Thread.sleep(100);
            RegisterResponse registerResponse = Client.getClient().registerResponse();
            LoginRegisterObject lro = new LoginRegisterObject(registerResponse);
            loginRegisterObject = Optional.of(lro);

        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }

}
