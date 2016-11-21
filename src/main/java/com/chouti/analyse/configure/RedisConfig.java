package com.chouti.analyse.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-09.
 *******************************************************************************/

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;


    @Bean
    @Primary
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        jedisConnectionFactory.setHostName(redisHost);
        jedisConnectionFactory.setPort(redisPort);
        return jedisConnectionFactory;
    }


    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100); //最大连接数
        poolConfig.setMaxIdle(50); //最大空闲数
        poolConfig.setMaxWaitMillis(1000); //最大建立链接等待时间
        return poolConfig;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        return new JedisPool(jedisPoolConfig, redisHost, redisPort);
    }

    @Bean
    @Primary
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}