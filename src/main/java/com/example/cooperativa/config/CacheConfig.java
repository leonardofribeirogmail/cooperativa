package com.example.cooperativa.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static com.example.cooperativa.util.CacheAlias.ASSOCIADOS;
import static com.example.cooperativa.util.CacheAlias.SESSOES;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(SESSOES, ASSOCIADOS);
    }
}
