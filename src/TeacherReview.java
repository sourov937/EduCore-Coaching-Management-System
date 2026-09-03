package com.educore.model;

import java.sql.Date;

public class TeacherReview {
    private int reviewId;
    private int studentId;
    private int teacherId;
    private int rating; // 1-5
    private String comment;
    private Date dateSubmitted;

    public TeacherReview() {
    }

    public TeacherReview(int reviewId, int studentId, int teacherId, int rating, String comment, Date dateSubmitted) {
        this.reviewId = reviewId;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.rating = rating;
        this.comment = comment;
        this.dateSubmitted = dateSubmitted;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }
}
