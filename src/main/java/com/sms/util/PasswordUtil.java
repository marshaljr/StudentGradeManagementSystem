package com.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtil — Utility class for password hashing.
 *
 * Uses SHA-256 to hash passwords before storing them in the database.
 * Never store plaintext passwords!
 */
public class PasswordUtil {

    /** Private constructor — utility class, not instantiable. */
    private PasswordUtil() {}

    /**
     * Hashes a plaintext password using SHA-256.
     *
     * @param plaintext the raw password to hash
     * @return hex-encoded SHA-256 hash string (64 characters)
     */
    public static String hash(String plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                plaintext.getBytes(StandardCharsets.UTF_8)
            );
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Verifies a plaintext password against a stored hash.
     *
     * @param plaintext    the raw password attempt
     * @param storedHash   the SHA-256 hash from the database
     * @return true if the password matches
     */
    public static boolean verify(String plaintext, String storedHash) {
        return hash(plaintext).equals(storedHash);
    }
}
