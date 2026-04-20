package com.rzodeczko.infrastructure.configuration.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SwaggerCorsProperties.
 */
class SwaggerCorsPropertiesTest {

    @Test
    void gettersAndSetters_shouldWork() {
        SwaggerCorsProperties props = new SwaggerCorsProperties();
        String allowedOrigins = "http://localhost:3000";
        boolean enabled = true;

        props.setAllowedOrigins(allowedOrigins);
        props.setEnabled(enabled);

        assertEquals(allowedOrigins, props.getAllowedOrigins());
        assertEquals(enabled, props.isEnabled());
    }
}
