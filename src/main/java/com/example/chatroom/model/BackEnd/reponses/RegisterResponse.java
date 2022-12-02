package com.example.chatroom.model.BackEnd.reponses;

import java.net.HttpCookie;

/**
 * 后端注册方法的返回类。记录注册操作的信息。
 */

public class RegisterResponse implements IResponse{
    private boolean success = false;
    private String errorMessage = "";

    //失败信息构造方法

    public RegisterResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
    }

    //成功信息构造方法
    public RegisterResponse(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }
}
