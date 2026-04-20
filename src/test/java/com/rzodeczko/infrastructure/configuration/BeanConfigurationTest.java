package com.rzodeczko.infrastructure.configuration;

import com.rzodeczko.application.service.InvoiceService;
import com.rzodeczko.domain.repository.InvoiceRepository;
import com.rzodeczko.infrastructure.configuration.properties.SwaggerCorsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BeanConfiguration.
 */
class BeanConfigurationTest {

    private final BeanConfiguration config = new BeanConfiguration();

    @Test
    void corsConfigurer_shouldCreateConfigurer() {
        SwaggerCorsProperties props = mock(SwaggerCorsProperties.class);
        when(props.getAllowedOrigins()).thenReturn("http://localhost:3000");

        WebMvcConfigurer configurer = config.corsConfigurer(props);

        assertNotNull(configurer);
    }

    @Test
    void restClientCustomizer_shouldCreateCustomizer() {
        assertNotNull(config.restClientCustomizer());
    }

    @Test
    void invoiceService_shouldCreateService() {
        InvoiceRepository repository = mock(InvoiceRepository.class);

        InvoiceService service = config.invoiceService(repository);

        assertNotNull(service);
    }
}
