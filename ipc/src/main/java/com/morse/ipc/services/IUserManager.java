package com.morse.ipc.services;

import com.morse.ipc.ann.ClassId;
import com.morse.ipc.bean.User;

@ClassId("com.morse.ipc.services.imp.UserManager")
public interface IUserManager {

    void setUser(User user);

    User getUser();

    User getUser(String name);

}
