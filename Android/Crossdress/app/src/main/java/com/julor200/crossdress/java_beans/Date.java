package com.julor200.crossdress.java_beans;

import java.io.Serializable;

/**
 * Javabean class for Date.
 */

public class Date implements Serializable {
    private int id;
    private String user;
    private int post;
    private String date;

    public Date() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
