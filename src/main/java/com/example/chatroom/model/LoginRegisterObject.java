package com.example.chatroom.model;

import com.example.chatroom.model.BackEnd.LoginResponse;
import com.example.chatroom.model.BackEnd.RegisterResponse;
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

    public LoginRegisterObject(Mode requestType, RegisterResponse response){
        this.requestType = requestType;
        wasError = !response.isSuccess();
        errorMessage = response.getErrorMessage();
        user = null;
    }
    public LoginRegisterObject(Mode requestType, LoginResponse response){
        this.requestType = requestType;
        wasError = !response.isSuccess();
        errorMessage = response.getErrorMessage();
        user = response.getUser();
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
