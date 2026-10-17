package com.smarthire.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authentication & Password Security Tests")
class SecurityTest {

    @Test
    @DisplayName("Should properly hash and verify valid passwords with jBCrypt")
    void testPasswordHashingAndVerification() {
        String plainPassword = "SecurePassword123!";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$"));

        // Verification of correct password
        assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));

        // Verification fails for incorrect password
        assertFalse(BCrypt.checkpw("WrongPassword!", hashedPassword));
    }
}
