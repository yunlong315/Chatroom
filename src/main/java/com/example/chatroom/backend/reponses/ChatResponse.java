package com.example.chatroom.backend.reponses;

public class ChatResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

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

    public String getTmpMsg() {
        return tmpMsg;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
