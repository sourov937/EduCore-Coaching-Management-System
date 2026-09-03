package com.educore.model;

public class Result {
    private int resultId;
    private int studentId;
    private int examId;
    private double marksObtained;
    private String grade;
    private String remarks;

    public Result() {
    }

    public Result(int resultId, int studentId, int examId, double marksObtained, String grade, String remarks) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.examId = examId;
        this.marksObtained = marksObtained;
        this.grade = grade;
        this.remarks = remarks;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
