package com.example.chatroom.model.BackEnd;

public class AddChatroomResponse
{
    private boolean success = false;
    private String errorMessage="";
    private final User user;
    private final ChatRoom chatrooms;
    //失败信息构造方法
    public AddChatroomResponse (String errorMessage)
    {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
        this.chatrooms=null;
    }
    //成功信息构造方法
    AddChatroomResponse(User user, ChatRoom chatrooms)
    {
        this.success = true;
        this.user = user;
        this.chatrooms=chatrooms;
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

    public ChatRoom getChatrooms() {
        return chatrooms;
    }
}
