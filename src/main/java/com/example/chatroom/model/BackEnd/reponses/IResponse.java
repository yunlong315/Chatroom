package com.example.chatroom.model.BackEnd.reponses;

import java.util.Objects;
/**
 *所有Response类实现的接口。Response必须实现getErrorMessage()和isSuccess()方法。
 */

public interface IResponse {
    public String getErrorMessage();
    public boolean isSuccess();
}

