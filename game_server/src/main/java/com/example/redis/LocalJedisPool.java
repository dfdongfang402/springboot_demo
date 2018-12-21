package com.example.redis;

import com.example.game.core.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author wang dongfang
 * @ClassName LocalJedisPool.java
 * @Description TODO
 * @createTime 2018年12月20日 17:27:00
 */
public enum LocalJedisPool implements IRedisPrivider {

    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(LocalJedisPool.class);

    RedisConfig redisConfig;

    private JedisPool jedisPool = null;


    LocalJedisPool() {
        redisConfig = SpringContextUtil.getBean(RedisConfig.class);
        initPool();
    }

    private void initPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getMaxActive());
        poolConfig.setMaxIdle(redisConfig.getMaxIdle());
        poolConfig.setMinIdle(redisConfig.getMinIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getMaxWait());

        jedisPool = new JedisPool(poolConfig,
                redisConfig.getHost(),
                redisConfig.getPort(),
                redisConfig.getTimeout(),
                null);
    }

    @Override
    public Jedis getJedisClient() {
        return jedisPool.getResource();
    }

}
