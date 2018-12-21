package com.example.redis;

import redis.clients.jedis.Jedis;

/**
 * @author wang dongfang
 * @ClassName IRedisPrivider.java
 * @Description TODO
 * @createTime 2018年12月20日 17:25:00
 */
public interface IRedisPrivider {
    Jedis getJedisClient();
}
