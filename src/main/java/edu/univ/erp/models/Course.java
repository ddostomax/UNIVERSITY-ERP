package edu.univ.erp.models;

public class Course {
    private long courseId;
    private String code;
    private String title;
    private int credits;

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        if (code != null && title != null) {
            return code + " - " + title;
        } else if (code != null) {
            return code;
        } else if (title != null) {
            return title;
        }
        return "Course #" + courseId;
    }
}


