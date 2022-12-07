package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;

public class ChatResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";
    private String message;

    //失败信息构造方法
    public ChatResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
    }

    //成功信息构造方法
    public ChatResponse(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public String getMessage() {
        return message;
    }
}
