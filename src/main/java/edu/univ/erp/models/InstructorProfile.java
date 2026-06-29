package edu.univ.erp.models;

public class InstructorProfile {
    private long userId;
    private String name;
    private String department;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name + (department != null ? " (" + department + ")" : "");
        }
        return "Instructor #" + userId;
    }
}


