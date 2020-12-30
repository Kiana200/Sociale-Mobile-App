package com.julor200.crossdress.java_beans;

import java.util.List;

/**
 * Javabean class for list with Users.
 */
public class UserList {
    private List<User> userList;

    public UserList() {
        //Required empty constructor
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
