package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.Client;
import com.example.chatroom.model.BackEnd.LoginResponse;
import com.example.chatroom.model.BackEnd.RegisterResponse;

import java.util.Optional;

public class LoginRegisterModel {
    private final Notifications notifications = new Notifications();
    //private final Client client;
    private Optional<LoginRegisterObject> loginRegisterObject = Optional.empty();

    public LoginRegisterModel() {
        Client client = Client.getClient();
        client.init();
    }

    public Optional<LoginRegisterObject> getLoginRegisterObject() {
        return loginRegisterObject;
    }

    public Optional<LoginRegisterObject> login(String userAccount, String passWord) {
        try {
            //TODO:调用真实后端接口
            //模拟登录请求失败
            LoginResponse loginResponse = new LoginResponse("login failed");
            LoginRegisterObject lro = new LoginRegisterObject(LoginRegisterObject.Mode.REGISTER,
                    loginResponse);
            loginRegisterObject = Optional.of(lro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }

    public Optional<LoginRegisterObject> register(String userAccount, String passWord) {
        try {
            //TODO:调用真实后端接口
            //TODO:把“userName”字符串换成真正的userName参数
            //模拟注册请求成功
            RegisterResponse registerResponse = Client.getClient().register(userAccount, passWord, "userName");
            LoginRegisterObject lro = new LoginRegisterObject(LoginRegisterObject.Mode.REGISTER,
                    registerResponse);
            loginRegisterObject = Optional.of(lro);

        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }

}
