package com.example.chatroom.backend.reponses;

/**
 * 更改房间名请求的返回信息类。
 */
public class ChangeChatroomNameResponse implements IResponse {
    private boolean success = false;
    private String errorMessage = "";
    private String tmpMsg = "";

    /**
     * 失败信息构造方法。
     * @param errorMessage 失败信息
     */
    public ChangeChatroomNameResponse(String errorMessage) {
        this.success = false;
        this.errorMessage += errorMessage;
    }

    /**
     * 成功信息构造方法。
     * @param success 是否成功。
     */
    public ChangeChatroomNameResponse(Boolean success) {
        this.success = true;
        this.errorMessage = null;
    }

    /**
     * 获取错误信息。
     * @return 错误信息。
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 请求是否成功。
     * @return 请求成功返回true,失败返回false。
     */
    public boolean isSuccess() {
        return success;
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
