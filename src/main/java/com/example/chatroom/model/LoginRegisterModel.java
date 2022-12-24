package com.example.chatroom.model;

import com.example.chatroom.backend.reponses.LoginResponse;
import com.example.chatroom.backend.reponses.RegisterResponse;
import com.example.chatroom.backend.socket.Client;
import com.example.chatroom.object.LoginRegisterObject;

import java.util.Optional;

/**
 * 登录注册界面的model层。
 */
public class LoginRegisterModel {
    private final Notifications notifications = new Notifications();
    private Optional<LoginRegisterObject> loginRegisterObject = Optional.empty();

    public LoginRegisterModel() {
        Client.getClient().init();
    }

    public Optional<LoginRegisterObject> getLoginRegisterObject() {
        return loginRegisterObject;
    }

    /**
     * 用户登录。
     *
     * @param userAccount-用户账号
     * @param passWord-用户密码
     * @return LoginRegisterObject此次请求的返回信息
     */
    public Optional<LoginRegisterObject> login(String userAccount, String passWord) {
        try {
            LoginResponse loginResponse = Client.getClient().login(userAccount, passWord);
            LoginRegisterObject lro = new LoginRegisterObject(loginResponse);
            loginRegisterObject = Optional.of(lro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }

    /**
     * 用户注册。
     *
     * @param userAccount-用户账号
     * @param passWord-用户密码
     * @param userName-用户名
     * @return LoginRegisterObject此次请求的返回信息
     */
    public Optional<LoginRegisterObject> register(String userAccount, String passWord, String userName) {
        try {
            RegisterResponse registerResponse = Client.getClient().register(userAccount, passWord, userName);
            LoginRegisterObject lro = new LoginRegisterObject(registerResponse);
            loginRegisterObject = Optional.of(lro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifications.publish(Notifications.EVENT_MODEL_UPDATE);
        return loginRegisterObject;
    }
}
