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
    //session超时时间
    @Value("${game.session.timeout}")
	private int sessionTimeout = 300;
    @Value("${game.user.timeout}")
	private int userTimeout = 7200;
}
