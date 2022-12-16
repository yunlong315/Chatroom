package com.example.chatroom.model.backend.reponses;

import com.example.chatroom.model.backend.ChatRoom;
import com.example.chatroom.model.backend.User;

public class JoinChatroomResponse implements IResponse
{
    private boolean success = false;
    private String errorMessage="";
    private final ChatRoom chatroom;
    private String tmpMsg;
    //失败信息构造方法
    public JoinChatroomResponse(String errorMessage)
    {
        this.success = false;
        this.errorMessage += errorMessage;
        this.chatroom=null;
    }
    //成功信息构造方法
    public JoinChatroomResponse(ChatRoom chatroom)
    {
        this.success = true;
        this.chatroom=chatroom;
        this.errorMessage = null;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public boolean isSuccess() {
        return success;
    }
    public ChatRoom getChatroom() {
        return chatroom;
    }

    public String getTmpMsg() {
        return tmpMsg;
    }

    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
