package com.example.chatroom.backend.reponses;

/**
 * 所有Response类实现的接口。Response必须实现getErrorMessage()和isSuccess()方法。
 */
public interface IResponse {
    /**
     * 获取错误信息。
     * @return 错误信息。
     */
    String getErrorMessage();

    /**
     * 检查请求是否成功。
     * @return 成功返回true,失败返回false
     */
    boolean isSuccess();
}

