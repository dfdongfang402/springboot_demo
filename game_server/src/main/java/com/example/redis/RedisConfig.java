package com.example.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedisConfig {

    @Value("${redis.host}")
	private String host = "localhost";
    @Value("${redis.port}")
	private int port = 6379;
    @Value("${redis.password}")
	private String password = "";
    @Value("${redis.timeout}")
	private int timeout = 0;
    @Value("${redis.pool.max-active}")
	private int maxActive = 8;
    @Value("${redis.pool.max-idle}")
	private int maxIdle = 8;
    @Value("${redis.pool.min-idle}")
	private int minIdle = 1;
    @Value("${redis.pool.max-wait}")
	private int maxWait = -1;

}
