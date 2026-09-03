package com.educore.model;

public class Teacher {
    private int teacherId;
    private String name;
    private String subject;
    private String phone;
    private String email;
    private String password;

    public Teacher() {
    }

    public Teacher(int teacherId, String name, String subject, String phone, String email, String password) {
        this.teacherId = teacherId;
        this.name = name;
        this.subject = subject;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    @Override
    public String toString() {
        return name + " (" + subject + ")";
    }
}
