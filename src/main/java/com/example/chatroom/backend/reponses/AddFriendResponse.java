package com.example.chatroom.backend.reponses;

import com.example.chatroom.backend.entity.User;

/**
 * 添加好友操作的返回信息类。
 */
public class AddFriendResponse implements IResponse {
    private final User user;
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    /**
     * 失败信息构造方法
     * @param errorMessage-失败信息
     */
    public AddFriendResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }

    /**
     * 成功信息构造方法。
     * @param user-要添加的好友
     */
    public AddFriendResponse(User user) {
        this.success = true;
        this.user = user;
        this.errorMessage = null;
    }

    /**
     * 获取错误信息。
     * @return 错误信息。
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 请求是否成功。
     * @return 请求成功返回true,否则返回false
     */
    @Override
    public boolean isSuccess() {
        return success;
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
     * @param tmpMsg 临时信息。
     */
    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }

    /**
     * 获取用户。
     * @return 用户对象。
     */
    public User getUser() {
        return user;
    }
}
