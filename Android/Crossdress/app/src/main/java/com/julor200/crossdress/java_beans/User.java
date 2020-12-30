package com.julor200.crossdress.java_beans;

import androidx.annotation.NonNull;

/**
 * Javabean class for Users
 */
public class User {
    private String username;
    private String email;
    private String password;
    private ReviewList reviews;

    public User() {
        //Required empty constructor
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ReviewList getReviews() {
        return reviews;
    }

    public void setReviews(ReviewList reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public String toString() {
        return username;
    }
}
