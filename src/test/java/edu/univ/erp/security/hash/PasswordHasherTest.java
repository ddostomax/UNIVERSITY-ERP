package edu.univ.erp.security.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashProducesNonPlaintextValue() {
        String plain = "s3cr3t!";
        String hash = PasswordHasher.hash(plain);

        assertNotNull(hash, "Hash should never be null");
        assertNotEquals(plain, hash, "Hash must not match the original password");
        assertTrue(hash.startsWith("$2"), "Bcrypt hashes should start with $2");
    }

    @Test
    void verifyReturnsTrueForCorrectPassword() {
        String plain = "correct-horse-battery-staple";
        String hash = PasswordHasher.hash(plain);

        assertTrue(PasswordHasher.verify(plain, hash));
        assertFalse(PasswordHasher.verify("wrong", hash));
    }

    @Test
    void verifyHandlesNullInputs() {
        String hash = PasswordHasher.hash("anything");

        assertFalse(PasswordHasher.verify(null, hash));
        assertFalse(PasswordHasher.verify("anything", null));
    }
}


