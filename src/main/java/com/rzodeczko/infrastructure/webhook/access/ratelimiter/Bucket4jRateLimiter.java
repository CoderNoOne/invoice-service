package com.rzodeczko.infrastructure.webhook.access.ratelimiter;

import com.rzodeczko.infrastructure.webhook.access.exception.WebhookRateLimitExceededException;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class Bucket4jRateLimiter implements ClientRateLimiter {

    private static final long LIMIT = 60;

    private final ProxyManager<String> proxyManager;

    @Override
    public void check(String clientId) {
        String key = "webhook:ratelimit:" + clientId;

        Supplier<BucketConfiguration> configurationSupplier = () ->
                BucketConfiguration.builder()
                        .addLimit(limit -> limit
                                .capacity(LIMIT)
                                .refillIntervally(LIMIT, Duration.ofMinutes(1))
                        )
                        .build();

        ConsumptionProbe probe = proxyManager.builder()
                .build(key, configurationSupplier)
                .tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            throw new WebhookRateLimitExceededException(
                    "Rate limit exceeded for clientId=%s. Retry after %d seconds"
                            .formatted(clientId, probe.getNanosToWaitForRefill() / 1_000_000_000)
            );
        }
    }
}
