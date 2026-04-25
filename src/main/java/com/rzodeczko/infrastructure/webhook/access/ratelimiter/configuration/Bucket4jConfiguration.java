package com.rzodeczko.infrastructure.webhook.access.ratelimiter.configuration;

import com.rzodeczko.infrastructure.configuration.properties.RedisProperties;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Bucket4jConfiguration {

    @Bean(destroyMethod = "shutdown")
    RedisClient redisClient(RedisProperties redisProperties) {
        return RedisClient.create(
                RedisURI.builder()
                        .withHost(redisProperties.host())
                        .withPort(redisProperties.port())
                        .build()
        );
    }

    @Bean(destroyMethod = "close")
    StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> redisConnection) {
        ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
                .withExpirationAfterWriteStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1))
                );

        return LettuceBasedProxyManager.builderFor(redisConnection)
                .withClientSideConfig(clientSideConfig)
                .build();
    }
}