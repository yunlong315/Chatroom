package com.example.chatroom.backend.reponses;

/**
 * 后端注册方法的返回类。记录注册操作的信息。
 */

public class RegisterResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    /**
     * 失败信息构造方法。
     * @param errorMessage 失败信息
     */
    public RegisterResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
    }

    /**
     * 成功信息构造方法。
     * @param success true
     */
    public RegisterResponse(boolean success) {
        this.success = success;
    }

    /**
     * 获取失败信息。
     * @return 失败信息。
     */
    public String getErrorMessage() {
        return errorMessage;
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
     * @param tmpMsg 临时信息
     */
    public void setTmpMsg(String tmpMsg) {
        this.tmpMsg = tmpMsg;
    }

    /**
     * 请求是否成功。
     * @return 成功返回true,失败返回false
     */
    public boolean isSuccess() {
        return success;
    }
}
