package com.julor200.crossdress.java_beans;

import java.io.Serializable;
import java.util.List;

/**
 * Javabean class for list with Posts.
 */
public class PostList implements Serializable {
    private List<Post> postList;

    public PostList() {
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
}
