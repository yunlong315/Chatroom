package com.example.chatroom.backend.reponses;

import com.example.chatroom.backend.entity.User;

/**
 * 后端登录方法的返回类。记录登录操作的信息。
 */
public class LoginResponse implements IResponse {
    private final User user;
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    /**
     *失败信息构造方法。
     * @param errorMessage 失败信息
     */
    public LoginResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
        this.user = null;
    }

    /**
     * 成功信息构造方法。
     * @param user 用户
     */
    public LoginResponse(User user) {
        this.success = true;
        this.user = user;
        this.errorMessage = null;
    }

    /**
     * 获取失败信息。
     * @return 失败信息
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
     * 获取用户。
     * @return 用户
     */
    public User getUser() {
        return user;
    }

    /**
     * 获取临时信息。
     * @return 临时信息
     */
    public String getTmpMsg() {
        return tmpMsg;
    }

    /**
     * 设置临时信息。
     * @param tmpMsg 临时信息
     */
    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }
}
