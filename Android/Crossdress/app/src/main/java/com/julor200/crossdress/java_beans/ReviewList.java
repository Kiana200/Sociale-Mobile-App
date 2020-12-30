package com.julor200.crossdress.java_beans;

import java.io.Serializable;
import java.util.List;

/**
 * Javabean class for list with Reviews.
 */
public class ReviewList implements Serializable {
    private List<Review> reviewList;

    public ReviewList() {
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
