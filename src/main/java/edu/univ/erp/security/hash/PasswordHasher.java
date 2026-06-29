package edu.univ.erp.security.hash;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Simple bcrypt wrapper for hashing and verifying passwords.
 */
public final class PasswordHasher {

    private static final int ROUNDS = 10;

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(ROUNDS));
    }

    public static boolean verify(String plainPassword, String hash) {
        if (plainPassword == null || hash == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hash);
    }
}


