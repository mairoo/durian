package kr.co.pincoin.api.infra.auth.support;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class DjangoPasswordEncoder implements PasswordEncoder {
    public static final String ALGORITHM = "pbkdf2_sha256";
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    public static final int DEFAULT_ITERATIONS = 870000;
    public static final int SALT_LENGTH = 16;
    public static final int HASH_LENGTH = 32;
    public static final int BITS_PER_BYTE = 8;

    public static final String PASSWORD_HASH_FORMAT = "%s$%d$%s$%s";
    public static final String HASH_SECTIONS_DELIMITER = "\\$";
    public static final int EXPECTED_SECTIONS_LENGTH = 4;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String
    getSalt(int length) {
        byte[] saltBytes = new byte[length];
        secureRandom.nextBytes(saltBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
    }

    public static String getSalt() {
        return getSalt(SALT_LENGTH);
    }

    public String
    encrypt(String plain, String salt, int iterations) throws NoSuchAlgorithmException,
                                                              InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(plain.toCharArray(),
                                         salt.getBytes(StandardCharsets.UTF_8),
                                         iterations,
                                         HASH_LENGTH * BITS_PER_BYTE); // key length 256 = 44 chars, 512 = 88 chars

        SecretKey secret = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM).generateSecret(keySpec);

        return new String(Base64.getEncoder().encode(secret.getEncoded()));
    }

    public String
    encode(String plain, String salt, int iterations) throws NoSuchAlgorithmException,
                                                             InvalidKeySpecException {
        String hash = encrypt(plain, salt, iterations);
        return String.format(PASSWORD_HASH_FORMAT, ALGORITHM, iterations, salt, hash);
    }

    @Override
    public String
    encode(CharSequence rawPassword) {
        try {
            return encode(rawPassword.toString(), getSalt(), DEFAULT_ITERATIONS);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
            return null;
        }
    }

    @Override
    public boolean
    matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split(HASH_SECTIONS_DELIMITER);

        if (parts.length != EXPECTED_SECTIONS_LENGTH) {
            return false;
        }

        try {
            return encode(rawPassword.toString(), parts[2], Integer.parseInt(parts[1])).equals(encodedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
            return false;
        }
    }
}