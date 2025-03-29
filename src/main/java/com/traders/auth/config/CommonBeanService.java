package com.traders.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.traders.auth.service.RedissonConfig;
import com.traders.common.properties.ConfigProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CommonBeanService {
    private final RedissonConfig redissonConfig;

    public CommonBeanService(RedissonConfig redissonConfig) {
        this.redissonConfig = redissonConfig;
    }
    @Bean
    public ConfigProperties getConfigProperties(){
        return new ConfigProperties();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures Instant is serialized as ISO-8601 string

        return mapper;
    }

    @Bean
    public RedissonClient getRedissonCLient(ConfigProperties configProperties){
        return Redisson.create(redissonConfig.getRedisConfig(configProperties));
    }
}
