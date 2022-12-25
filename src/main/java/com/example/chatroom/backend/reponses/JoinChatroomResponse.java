package com.example.chatroom.backend.reponses;

import com.example.chatroom.backend.entity.ChatRoom;

/**
 * 加入聊天室请求的返回信息类。
 */
public class JoinChatroomResponse implements IResponse {
    private final ChatRoom chatroom;
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg;

    /**
     * 失败信息构造方法。
     * @param errorMessage 失败信息
     */
    public JoinChatroomResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.chatroom = null;
    }

    /**
     * 成功信息构造方法。
     * @param chatroom 聊天室
     */
    public JoinChatroomResponse(ChatRoom chatroom) {
        this.success = true;
        this.chatroom = chatroom;
        this.errorMessage = null;
    }

    /**
     * 获取错误信息。
     * @return 错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 请求是否成功。
     * @return 成功返回true,失败返回false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取聊天室。
     * @return 聊天室
     */
    public ChatRoom getChatroom() {
        return chatroom;
    }

    /**
     * 获取临时信息。
     * @return 临时信息。
     */
    public String getTmpMsg() {
        return tmpMsg;
    }

    /**
     * 设置临时信息。
     * @param tmpMsg
     */
    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
