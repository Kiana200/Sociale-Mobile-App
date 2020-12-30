package com.julor200.crossdress.java_beans;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Javabean class for Reviews.
 */

public class Review implements Serializable {
    private int id;
    private String review;
    private String publisher;
    private Post post;

    public Review() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @NonNull
    @Override
    public String toString() {
        return publisher + ": " + review;
    }
}
