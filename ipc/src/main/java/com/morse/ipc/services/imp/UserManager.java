package com.morse.ipc.services.imp;

import com.morse.ipc.bean.User;
import com.morse.ipc.services.IUserManager;

public class UserManager implements IUserManager {

    private User user;

    private static UserManager instance = new UserManager();

    private UserManager() {}

    public static UserManager getInstance() {
        return instance;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
