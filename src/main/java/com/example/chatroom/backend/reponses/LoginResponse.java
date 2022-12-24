package com.example.chatroom.backend.reponses;

import com.example.chatroom.backend.entity.User;

/**
 * 后端登录方法的返回类。记录登录操作的信息。
 */

public class LoginResponse implements IResponse {
    private final User user;
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";  // tmpMsg用于后端暂时储存返回信息

    //失败信息构造方法
    public LoginResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }

    //成功信息构造方法
    public LoginResponse(User user) {
        this.success = true;
        this.user = user;
        this.errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
