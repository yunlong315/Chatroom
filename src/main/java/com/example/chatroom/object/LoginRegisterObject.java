package com.example.chatroom.object;

import com.example.chatroom.backend.entity.User;
import com.example.chatroom.backend.reponses.IResponse;
import com.example.chatroom.backend.reponses.LoginResponse;
import com.example.chatroom.backend.reponses.RegisterResponse;

/**
 * 用户登录和注册请求的返回信息。
 */
public class LoginRegisterObject {
    private final Boolean wasError;
    private final String errorMessage;
    private final User user;
    private final Mode requestType;

    public LoginRegisterObject(IResponse response) {
        wasError = !response.isSuccess();
        errorMessage = response.getErrorMessage();
        if (response instanceof RegisterResponse) {
            requestType = Mode.REGISTER;
            user = null;
        } else if (response instanceof LoginResponse loginResponse) {
            requestType = Mode.LOGIN;
            user = loginResponse.getUser();
        } else {
            user = null;
            requestType = null;
        }
    }

    public Boolean getWasError() {
        return wasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean isLogin() {
        return requestType == Mode.LOGIN;
    }

    public Boolean isRegister() {
        return requestType == Mode.REGISTER;
    }

    public User getUser() {
        return user;
    }

    enum Mode {
        LOGIN,
        REGISTER
    }
}
