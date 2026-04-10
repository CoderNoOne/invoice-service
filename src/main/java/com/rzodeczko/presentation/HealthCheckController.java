package com.rzodeczko.presentation;

import com.rzodeczko.presentation.dto.HealthCheckResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing a basic health-check endpoint.
 */
@RestController
public class HealthCheckController {
    /**
     * Returns service availability status.
     *
     * @return HTTP 200 with {@link HealthCheckResponseDto}
     */
    @GetMapping("/")
    public ResponseEntity<HealthCheckResponseDto> healthCheck() {
        return ResponseEntity.ok(new HealthCheckResponseDto("INVOICE SERVICE OK"));
    }
}
