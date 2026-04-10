package com.rzodeczko.configuration;

import com.rzodeczko.configuration.properties.InvoicesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InvoicesProperties.class)
public class BeanConfiguration {
}
