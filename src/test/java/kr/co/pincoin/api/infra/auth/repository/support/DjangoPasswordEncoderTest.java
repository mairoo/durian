package kr.co.pincoin.api.infra.auth.repository.support;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import kr.co.pincoin.api.infra.auth.support.DjangoPasswordEncoder;
import org.junit.jupiter.api.Test;

class DjangoPasswordEncoderTest {

  @Test
  void testEncode() {
    DjangoPasswordEncoder encoder = new DjangoPasswordEncoder();
    String rawPassword = "mypassword";
    String encodedPassword = encoder.encode(rawPassword);

    String[] parts = encodedPassword.split("\\$");
    assertAll(
        () -> assertEquals(4, parts.length),
        () -> assertEquals(DjangoPasswordEncoder.ALGORITHM, parts[0]),
        () -> assertEquals(String.valueOf(DjangoPasswordEncoder.DEFAULT_ITERATIONS), parts[1]),
        () -> assertEquals(16, Base64.getDecoder().decode(parts[2]).length),
        () -> assertEquals(32, Base64.getDecoder().decode(parts[3]).length));
  }

  @Test
  void testMatches() {
    DjangoPasswordEncoder encoder = new DjangoPasswordEncoder();
    String rawPassword = "mypassword";
    String encodedPassword = encoder.encode(rawPassword);

    assertTrue(encoder.matches(rawPassword, encodedPassword));
    assertFalse(encoder.matches("wrongpassword", encodedPassword));
  }

  @Test
  void testMatchesWithInvalidEncodedPassword() {
    DjangoPasswordEncoder encoder = new DjangoPasswordEncoder();

    assertFalse(encoder.matches("mypassword", ""));
    assertFalse(encoder.matches("mypassword", "invalid_format"));
  }
}
