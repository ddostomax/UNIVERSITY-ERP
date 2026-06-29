package edu.univ.erp.security.session;

/**
 * Represents the currently logged-in user in memory.
 */
public class UserSession {

    private final long userId;
    private final String username;
    private final String role; // ADMIN / INSTRUCTOR / STUDENT

    public UserSession(long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}


