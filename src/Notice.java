package com.educore.model;

import java.sql.Date;

public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private Date datePosted;
    private String targetRole; // All, Teacher, Student, Guardian

    public Notice() {
    }

    public Notice(int noticeId, String title, String content, Date datePosted, String targetRole) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.datePosted = datePosted;
        this.targetRole = targetRole;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    @Override
    public String toString() {
        return title;
    }
}
