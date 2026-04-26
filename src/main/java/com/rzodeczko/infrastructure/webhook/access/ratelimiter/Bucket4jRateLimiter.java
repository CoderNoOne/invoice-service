package com.rzodeczko.infrastructure.webhook.access.ratelimiter;

import com.rzodeczko.infrastructure.configuration.properties.WebhookClientsProperties;
import com.rzodeczko.infrastructure.webhook.access.exception.WebhookRateLimitExceededException;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Applies per-client webhook rate limits using Bucket4j.
 */
@Component
@RequiredArgsConstructor
public class Bucket4jRateLimiter implements ClientRateLimiter {

    private final ProxyManager<String> proxyManager;
    private final WebhookClientsProperties webhookClientsProperties;

    /**
     * Checks whether the client may perform another request.
     */
    @Override
    public void check(String clientId) {
        String key = "webhook:ratelimit:" + clientId;
        WebhookClientsProperties.ClientConfig webhookClientConfig = webhookClientsProperties.clients().get(clientId);
        if (Objects.isNull(webhookClientConfig) || webhookClientConfig.requestsPerMinuteLimit() <= 0) {
            return; // No rate limit configured for this client
        }

        int rateLimit = webhookClientConfig.requestsPerMinuteLimit();

        ConsumptionProbe probe = proxyManager.builder()
                .build(key, configurationSupplier(rateLimit))
                .tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            long retryAfterSeconds = Math.max(
                    1,
                    (probe.getNanosToWaitForRefill() + 999_999_999L) / 1_000_000_000L
            );

            throw new WebhookRateLimitExceededException(
                    "Rate limit exceeded for clientId=%s. Try again in %d seconds"
                            .formatted(clientId, retryAfterSeconds)
            );
        }
    }

    /**
     * Creates a lazily evaluated bucket configuration with greedy refill for the given per-minute rate limit.
     */
    private Supplier<BucketConfiguration> configurationSupplier(int rateLimit) {
        return () -> BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(rateLimit)
                        .refillGreedy(rateLimit, Duration.ofMinutes(1))
                )
                .build();
    }


}
