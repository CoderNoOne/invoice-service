package com.rzodeczko.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for HealthCheckController using standalone MockMvc setup.
 */
class HealthCheckControllerIT {

    @Test
    void healthCheck_shouldReturn200WithMessage() throws Exception {
        HealthCheckController controller = new HealthCheckController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("INVOICE SERVICE OK"));
    }
}
