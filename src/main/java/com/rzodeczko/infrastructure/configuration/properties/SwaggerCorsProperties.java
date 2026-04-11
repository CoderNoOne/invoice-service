package com.rzodeczko.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
public class SwaggerCorsProperties {
    private String swaggerOrigin;
}
