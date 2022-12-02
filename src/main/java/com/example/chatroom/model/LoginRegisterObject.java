package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.reponses.IResponse;
import com.example.chatroom.model.BackEnd.reponses.LoginResponse;
import com.example.chatroom.model.BackEnd.reponses.RegisterResponse;
import com.example.chatroom.model.BackEnd.User;

public class LoginRegisterObject {
    private final Boolean wasError;
    private final String errorMessage;
    private final User user;
    private final Mode requestType;

    enum Mode{
        LOGIN,
        REGISTER;
    }

    public LoginRegisterObject(IResponse response) {
        wasError = !response.isSuccess();
        errorMessage = response.getErrorMessage();
        if (response instanceof RegisterResponse) {
            requestType = Mode.REGISTER;
            user = null;
        }
        else if (response instanceof LoginResponse loginResponse) {
            requestType = Mode.LOGIN;
            user = loginResponse.getUser();
        }
        else
        {
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
    public Boolean isLogin()
    {
        return requestType == Mode.LOGIN;
    }
    public Boolean isRegister()
    {
        return requestType == Mode.REGISTER;
    }

    public User getUser() {
        return user;
    }
}
