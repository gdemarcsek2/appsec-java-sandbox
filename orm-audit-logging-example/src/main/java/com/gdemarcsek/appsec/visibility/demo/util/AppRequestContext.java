package com.gdemarcsek.appsec.visibility.demo.util;

import com.gdemarcsek.appsec.visibility.demo.core.User;

public class AppRequestContext {
    private static ThreadLocal<User> currentUser = new InheritableThreadLocal<User>();

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }
}
