package com.example.chatroom.util;

public class PatternUtil {
    private static final String UserAccountPattern = "^\\w{5,16}$";
    private static final String UserPasswordPattern = "^\\w{5,16}$";
    private static final String UserNamePattern = "^.{1,50}$";
    public static String getUserAccountPattern() {
        return UserAccountPattern;
    }
    public static String getUserPasswordPattern() {
        return UserPasswordPattern;
    }
    public static String getUserNamePattern() {
        return UserNamePattern;
    }
}
