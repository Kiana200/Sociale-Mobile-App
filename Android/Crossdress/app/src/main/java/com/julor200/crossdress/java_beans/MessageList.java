package com.julor200.crossdress.java_beans;

import java.util.List;

/**
 * Javabean class for list with Messages.
 */
public class MessageList {
    private List<Message> messageList;

    public MessageList() {
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
