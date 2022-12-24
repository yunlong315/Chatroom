package com.example.chatroom.backend.reponses;

/**
 * 所有Response类实现的接口。Response必须实现getErrorMessage()和isSuccess()方法。
 */

public interface IResponse {
    String getErrorMessage();

    boolean isSuccess();
}

