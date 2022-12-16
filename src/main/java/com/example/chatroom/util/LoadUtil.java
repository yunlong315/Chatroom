package com.example.chatroom.util;

import com.example.chatroom.model.backend.ChatRoom;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class LoadUtil {
    //反序列化
    public static Object loadObject(byte[] objByte)
    {
        ByteArrayInputStream bi = new ByteArrayInputStream(objByte);
        Object o = null;
        try (bi; ObjectInputStream oi = new ObjectInputStream(bi)) {
            //bytearray to object
            o = oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }


}
