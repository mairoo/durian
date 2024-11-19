package kr.co.pincoin.api.scratch;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleStringTest {

    @Test
    void testString() {
        // Given
        String text = "Hello";

        // Then
        assertThat(text).isEqualTo("Hello");
    }
}