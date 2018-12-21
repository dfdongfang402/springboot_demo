package com.example.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * @author wang dongfang
 * @ClassName RemoteJedisPool.java
 * @Description TODO
 * @createTime 2018年12月20日 17:27:00
 */
public class RemoteJedisPool implements IRedisPrivider{
    private int serverId;

    private static final Logger logger = LoggerFactory.getLogger(RemoteJedisPool.class);

    private RedisConfig redisConfig = LocalJedisPool.INSTANCE.redisConfig;

    public RemoteJedisPool(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public Jedis getJedisClient() {
        //TODO 从配置读取指定redis服务器的ip和端口
        String ip = "ip";
        int port = 1;
        Jedis jedis = new Jedis(ip, port, redisConfig.getTimeout());
        return jedis;
    }
}
