package com.dm.docmind.context;

public class GlobalUserContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static String getUserId() {
        return currentUser.get();
    }

    public static void setUserId(String userId) {
        currentUser.set(userId);
    }

    public static void clear() {
        currentUser.remove();
    }
}
