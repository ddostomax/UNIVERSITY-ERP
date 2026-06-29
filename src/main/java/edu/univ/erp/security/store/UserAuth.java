package edu.univ.erp.security.store;

/**
 * Represents a row in users_auth.
 */
public class UserAuth {

    private long userId;
    private String username;
    private String role;
    private String passwordHash;
    private String status;
    private int loginAttempts;
    private java.sql.Timestamp lockedUntil;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public java.sql.Timestamp getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(java.sql.Timestamp lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}


