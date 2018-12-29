package com.example.game.core;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GameConfig {

    //玩家个人请求队列长度
    @Value("${game.request.queueMax}")
	private int requestQueueMax = 100;
    @Value("${game.request.slowMillis}")
	private int requestSlowMillis = 1000;
    @Value("${game.player.cache.max_size}")
	private int playerCacheMax = 10000;
    @Value("${game.player.cache.expire_after_access}")
	private int playerCacheExpireAfterAccess = 10;
    @Value("${game.player.cache.expire_after_write}")
	private int playerCacheExpireAfterWrite = 10;

}
