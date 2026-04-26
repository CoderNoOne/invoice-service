package com.rzodeczko.infrastructure.webhook.access.aop;

import com.rzodeczko.infrastructure.webhook.access.TrustedWebhookClient;

import java.lang.annotation.*;

/**
 * Marks a method as requiring webhook security validation for the specified trusted clients.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebhookSecured {
    TrustedWebhookClient[] value();
}
