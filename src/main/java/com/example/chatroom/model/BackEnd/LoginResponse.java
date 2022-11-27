package com.example.chatroom.model.BackEnd;

/**
 *后端登录方法的返回类。记录登录操作的信息。
 */

public class LoginResponse {
    private boolean success = false;
    private String errorMessage="";
    private final User user;
    //失败信息构造方法
    public LoginResponse(String errorMessage)
    {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }
    //成功信息构造方法
    LoginResponse(User user)
    {
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
}
