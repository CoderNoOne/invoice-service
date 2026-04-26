package com.rzodeczko.infrastructure.configuration.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FakturowniaProperties.
 */
class FakturowniaPropertiesTest {

    @Test
    void constructor_shouldCreateProperties() {
        String url = "http://example.com";
        String token = "token123";
        FakturowniaProperties props = new FakturowniaProperties(url, token);
        assertEquals(url, props.url());
        assertEquals(token, props.token());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        FakturowniaProperties props1 = new FakturowniaProperties("url", "sharedSecret");
        FakturowniaProperties props2 = new FakturowniaProperties("url", "sharedSecret");
        assertEquals(props1, props2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        FakturowniaProperties props1 = new FakturowniaProperties("url", "sharedSecret");
        FakturowniaProperties props2 = new FakturowniaProperties("url", "sharedSecret");
        assertEquals(props1.hashCode(), props2.hashCode());
    }
}
