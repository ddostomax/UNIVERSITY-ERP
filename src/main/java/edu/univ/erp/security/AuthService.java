package edu.univ.erp.security;

import edu.univ.erp.security.hash.PasswordHasher;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.security.store.UserAuth;
import edu.univ.erp.security.store.UserAuthDao;

/**
 * Service responsible for authenticating users against the Auth DB.
 */
public class AuthService {

    private final UserAuthDao userAuthDao = new UserAuthDao();

    public AuthResult login(String username, String password) {
        try {
            UserAuth user = userAuthDao.findByUsername(username);
            if (user == null) {
                return AuthResult.failure("Incorrect username or password.");
            }
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                return AuthResult.failure("Account is not active.");
            }

            // Check if account is locked
            if (user.getLockedUntil() != null) {
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                if (user.getLockedUntil().after(now)) {
                    long minutesRemaining = (user.getLockedUntil().getTime() - now.getTime()) / (60 * 1000);
                    return AuthResult.failure("Account is locked. Please try again in " + minutesRemaining + " minute(s).");
                } else {
                    // Lock expired, reset
                    userAuthDao.resetLoginAttempts(user.getUserId());
                    user.setLockedUntil(null);
                    user.setLoginAttempts(0);
                }
            }

            if (!PasswordHasher.verify(password, user.getPasswordHash())) {
                // Increment login attempts
                userAuthDao.incrementLoginAttempts(user.getUserId());
                int attempts = user.getLoginAttempts() + 1;
                
                if (attempts >= 5) {
                    // Lock account for 30 minutes
                    java.sql.Timestamp lockUntil = new java.sql.Timestamp(System.currentTimeMillis() + (30 * 60 * 1000));
                    userAuthDao.lockAccount(user.getUserId(), lockUntil);
                    return AuthResult.failure("Too many failed login attempts. Account locked for 30 minutes.");
                } else {
                    int remaining = 5 - attempts;
                    return AuthResult.failure("Incorrect username or password. " + remaining + " attempt(s) remaining.");
                }
            }

            // Successful login - reset attempts
            userAuthDao.resetLoginAttempts(user.getUserId());

            UserSession session = new UserSession(user.getUserId(), user.getUsername(), user.getRole());
            SessionManager.setCurrentSession(session);

            return AuthResult.success(session);
        } catch (Exception e) {
            return AuthResult.failure("Login failed due to a system error: " + e.getMessage());
        }
    }

    public void logout() {
        SessionManager.clear();
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final UserSession session;

        private AuthResult(boolean success, String message, UserSession session) {
            this.success = success;
            this.message = message;
            this.session = session;
        }

        public static AuthResult success(UserSession session) {
            return new AuthResult(true, null, session);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public UserSession getSession() {
            return session;
        }
    }
}


