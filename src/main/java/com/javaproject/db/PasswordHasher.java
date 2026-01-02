package com.javaproject.db;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public final class PasswordHasher {
    private static final SecureRandom RNG = new SecureRandom();

    // Reasonable defaults for demo apps. Can be increased later.
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final int ITERATIONS = 120_000;

    private PasswordHasher() {
    }

    public static PasswordHash hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        byte[] derived = pbkdf2(password, salt, ITERATIONS, HASH_BYTES);
        return new PasswordHash(salt, derived, ITERATIONS);
    }

    public static boolean verify(char[] password, PasswordHash stored) {
        byte[] derived = pbkdf2(password, stored.salt(), stored.iterations(), stored.hash().length);
        return constantTimeEquals(derived, stored.hash());
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBytes) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBytes * 8);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return f.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    public record PasswordHash(byte[] salt, byte[] hash, int iterations) {
    }
}
