package com.example.chatroom.backend.reponses;

import com.example.chatroom.backend.entity.User;

public class AddFriendResponse implements IResponse {
    private final User user;
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    //失败信息构造方法
    public AddFriendResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }

    //成功信息构造方法
    public AddFriendResponse(User user) {
        this.success = true;
        this.user = user;
        this.errorMessage = null;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }

    public User getUser() {
        return user;
    }
}
