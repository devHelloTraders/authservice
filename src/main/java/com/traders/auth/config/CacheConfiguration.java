package com.traders.auth.config;

import com.traders.common.config.RedissonConfig;
import com.traders.common.utils.CommonValidations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration extends com.traders.common.config.CacheConfiguration {
    private final Long sessionTTL;
    private final RedissonConfig redissonConfig;
    public CacheConfiguration(@Value("redis.session.ttl") String sessionTTLValue, RedissonConfig redissonConfig) {
        super(redissonConfig);
        this.sessionTTL = CommonValidations.getNumber(sessionTTLValue, Long.class);
        this.redissonConfig = redissonConfig;
    }



}
