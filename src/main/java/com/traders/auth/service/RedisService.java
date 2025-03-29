package com.traders.auth.service;

import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisService extends com.traders.common.service.RedisService {

    public RedisService(javax.cache.CacheManager cacheManager, org.redisson.api.RedissonClient redissonClient, ConfigProperties configProperties) {
        super(cacheManager, redissonClient, configProperties);
    }
}
