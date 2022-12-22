package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.ChatRoom;

public class ChangeChatroomNameResponse implements IResponse{
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    //失败信息构造方法
    public ChangeChatroomNameResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
    }

    //成功信息构造方法
    public ChangeChatroomNameResponse(Boolean success) {
        this.success = true;
        this.errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
