package com.julor200.crossdress.java_beans;

/**
 * Javabean class for Blacklist. Not currently used since no function on server returns
 * Blacklist.
 */
public class Blacklist {
    private int id;
    private String jti;
    private Boolean revoked;
    private User user;

    public Blacklist() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
