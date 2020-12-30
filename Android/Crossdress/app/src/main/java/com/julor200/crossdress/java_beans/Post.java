package com.julor200.crossdress.java_beans;

import java.io.Serializable;

/**
 * Javabean class for Posts.
 */
public class Post implements Serializable {
    private int id;
    private String rubric;
    private String size;
    private String posted_by;
    private String category;
    private String reviewed_by;
    private DateList dates;
    private ReviewList reviews;
    private String photo;
    private String description;
    private String latitude;
    private String longitude;

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public String getRubric() {
        return rubric;
    }

    public void setRubric(String rubric) {
        this.rubric = rubric;
    }

    public ReviewList getReviews() {
        return reviews;
    }

    public void setReviews(ReviewList reviews) {
        this.reviews = reviews;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReviewed_by() {
        return reviewed_by;
    }

    public void setReviewed_by(String reviewed_by) {
        this.reviewed_by = reviewed_by;
    }

    public DateList getDates() {
        return dates;
    }

    public void setDates(DateList dates) {
        this.dates = dates;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
