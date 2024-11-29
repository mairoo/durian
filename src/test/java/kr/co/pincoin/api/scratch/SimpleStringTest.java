package kr.co.pincoin.api.scratch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SimpleStringTest {

  @Test
  void testString() {
    // Given
    String text = "Hello";

    // Then
    assertThat(text).isEqualTo("Hello");
  }
}
