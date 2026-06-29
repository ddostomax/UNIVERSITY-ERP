package edu.univ.erp.security.store;

import edu.univ.erp.data.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for users_auth table in Auth DB.
 */
public class UserAuthDao {

    private static final String SELECT_BY_USERNAME =
            "SELECT user_id, username, role, password_hash, status, login_attempts, locked_until FROM users_auth WHERE username = ?";
    
    private static final String INSERT_USER =
            "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_LOGIN_ATTEMPTS =
            "UPDATE users_auth SET login_attempts = ? WHERE user_id = ?";
    
    private static final String RESET_LOGIN_ATTEMPTS =
            "UPDATE users_auth SET login_attempts = 0, locked_until = NULL WHERE user_id = ?";
    
    private static final String LOCK_ACCOUNT =
            "UPDATE users_auth SET locked_until = ? WHERE user_id = ?";
    
    private static final String UPDATE_PASSWORD =
            "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

    public UserAuth findByUsername(String username) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserAuth user = new UserAuth();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setStatus(rs.getString("status"));
                    user.setLoginAttempts(rs.getInt("login_attempts"));
                    java.sql.Timestamp lockedUntil = rs.getTimestamp("locked_until");
                    user.setLockedUntil(lockedUntil);
                    return user;
                }
            }
        }
        return null;
    }

    public long createUser(String username, String role, String passwordHash) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            ps.setString(4, "ACTIVE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create user");
    }

    public void incrementLoginAttempts(long userId) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LOGIN_ATTEMPTS)) {
            // Get current attempts first
            UserAuth user = findByUserId(userId);
            if (user != null) {
                int newAttempts = user.getLoginAttempts() + 1;
                ps.setInt(1, newAttempts);
                ps.setLong(2, userId);
                ps.executeUpdate();
            }
        }
    }

    public void resetLoginAttempts(long userId) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(RESET_LOGIN_ATTEMPTS)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public void lockAccount(long userId, java.sql.Timestamp lockedUntil) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(LOCK_ACCOUNT)) {
            ps.setTimestamp(1, lockedUntil);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(long userId, String newPasswordHash) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public UserAuth findByUserId(long userId) throws SQLException {
        try (Connection conn = DBConnector.getAuthDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT user_id, username, role, password_hash, status, login_attempts, locked_until FROM users_auth WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserAuth user = new UserAuth();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setStatus(rs.getString("status"));
                    user.setLoginAttempts(rs.getInt("login_attempts"));
                    java.sql.Timestamp lockedUntil = rs.getTimestamp("locked_until");
                    user.setLockedUntil(lockedUntil);
                    return user;
                }
            }
        }
        return null;
    }
}


