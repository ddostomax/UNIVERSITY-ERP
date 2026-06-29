package edu.univ.erp.models;

public class Enrollment {
    private long enrollmentId;
    private long studentId;
    private long sectionId;
    private String status; // ENROLLED / DROPPED etc.
    private java.time.LocalDate enrollmentDate;

    public long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.time.LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(java.time.LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}


