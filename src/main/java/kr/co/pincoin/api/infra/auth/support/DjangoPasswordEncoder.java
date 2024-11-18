package kr.co.pincoin.api.infra.auth.support;


import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DjangoPasswordEncoder implements PasswordEncoder {
    // Algorithm Constants
    public static final String ALGORITHM = "pbkdf2_sha256";
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    // Configuration Constants
    public static final int DEFAULT_ITERATIONS = 600000; // Django 4.x default
    public static final int SALT_LENGTH = 12;
    public static final int HASH_LENGTH = 32;
    public static final int BITS_PER_BYTE = 8;

    // Format Constants
    public static final String PASSWORD_HASH_FORMAT = "%s$%d$%s$%s";
    public static final String HASH_SECTIONS_DELIMITER = "\\$";
    public static final int EXPECTED_SECTIONS_LENGTH = 4;

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = generateSalt();
        byte[] hash = hashPassword(rawPassword.toString(), salt, DEFAULT_ITERATIONS);

        String saltString = Base64.getEncoder().encodeToString(salt);
        String hashString = Base64.getEncoder().encodeToString(hash);

        return String.format(PASSWORD_HASH_FORMAT,
                             ALGORITHM,
                             DEFAULT_ITERATIONS,
                             saltString.trim(),
                             hashString.trim());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        String[] parts = encodedPassword.split(HASH_SECTIONS_DELIMITER);
        if (parts.length != EXPECTED_SECTIONS_LENGTH) {
            return false;
        }

        try {
            String algorithm = parts[0];
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            if (!algorithm.equals(ALGORITHM)) {
                return false;
            }

            byte[] actualHash = hashPassword(rawPassword.toString(), salt, iterations);
            return constantTimeArrayEquals(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        try {
            SecureRandom sr = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate salt", e);
        }
    }

    private byte[] hashPassword(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    HASH_LENGTH * BITS_PER_BYTE
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private boolean constantTimeArrayEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}