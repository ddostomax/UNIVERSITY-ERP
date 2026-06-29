package edu.univ.erp.security.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @AfterEach
    void tearDown() {
        SessionManager.clear();
    }

    @Test
    void setAndGetCurrentSession() {
        UserSession session = new UserSession(1L, "admin1", "ADMIN");
        SessionManager.setCurrentSession(session);

        UserSession retrieved = SessionManager.getCurrentSession();
        assertNotNull(retrieved);
        assertEquals(session.getUserId(), retrieved.getUserId());
        assertEquals(session.getUsername(), retrieved.getUsername());
        assertEquals(session.getRole(), retrieved.getRole());
    }

    @Test
    void clearRemovesSession() {
        SessionManager.setCurrentSession(new UserSession(2L, "stu1", "STUDENT"));
        SessionManager.clear();

        assertNull(SessionManager.getCurrentSession());
    }
}


