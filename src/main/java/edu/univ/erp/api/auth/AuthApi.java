package edu.univ.erp.api.auth;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.security.AuthService;
import edu.univ.erp.security.hash.PasswordHasher;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.security.store.UserAuthDao;

/**
 * API for authentication-related operations.
 */
public class AuthApi {

    private final AuthService authService = new AuthService();
    private final UserAuthDao userAuthDao = new UserAuthDao();

    public ApiResponse<Void> changePassword(String currentPassword, String newPassword) {
        try {
            UserSession session = SessionManager.getCurrentSession();
            if (session == null) {
                return ApiResponse.failure("No active session.");
            }

            // Verify current password
            var user = userAuthDao.findByUserId(session.getUserId());
            if (user == null) {
                return ApiResponse.failure("User not found.");
            }

            if (!PasswordHasher.verify(currentPassword, user.getPasswordHash())) {
                return ApiResponse.failure("Current password is incorrect.");
            }

            // Validate new password
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ApiResponse.failure("New password cannot be empty.");
            }

            if (newPassword.length() < 6) {
                return ApiResponse.failure("New password must be at least 6 characters long.");
            }

            // Update password
            String newPasswordHash = PasswordHasher.hash(newPassword);
            userAuthDao.updatePassword(session.getUserId(), newPasswordHash);

            return ApiResponse.success("Password changed successfully.", null);
        } catch (Exception e) {
            return ApiResponse.failure("Failed to change password: " + e.getMessage());
        }
    }
}

