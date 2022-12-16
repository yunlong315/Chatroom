package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.User;

public class AddFriendResponse implements IResponse{
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";
    private final User user;

    //失败信息构造方法
    public AddFriendResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }

    //成功信息构造方法
    public AddFriendResponse(User user) {
        this.success = success;
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

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public User getUser() {
        return user;
    }
}
