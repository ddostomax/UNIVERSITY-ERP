package edu.univ.erp.security.session;

/**
 * Simple in-memory session holder for the desktop app.
 */
public final class SessionManager {

    private static volatile UserSession currentSession;

    private SessionManager() {
    }

    public static void setCurrentSession(UserSession session) {
        currentSession = session;
    }

    public static UserSession getCurrentSession() {
        return currentSession;
    }

    public static void clear() {
        currentSession = null;
    }
}


