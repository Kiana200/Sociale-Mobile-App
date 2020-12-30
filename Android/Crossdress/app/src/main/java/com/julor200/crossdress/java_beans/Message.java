package com.julor200.crossdress.java_beans;

import androidx.annotation.NonNull;

/**
 * Javabean class for Messages.
 */

public class Message {
    private int id;
    private String message;
    private UserList read_by;
    private String sender;
    private String receiver;

    public Message(int id, String message, UserList readBy, String sender, String receiver) {
        this.id = id;
        this.message = message;
        this.read_by = readBy;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserList getReadBy() {
        return read_by;
    }

    public void setReadBy(UserList readBy) {
        read_by = readBy;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @NonNull
    @Override
    public String toString() {
        return sender + ": " + message;
    }
}
