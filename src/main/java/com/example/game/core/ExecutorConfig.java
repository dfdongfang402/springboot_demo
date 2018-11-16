package com.example.game.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    //消息线程池
    @Value("${threadpool.msg.core}")
	private int msgCoreThreadSize = 16;
    @Value("${threadpool.msg.max}")
	private int msgMaxThreadSize = 32;
    @Value("${threadpool.msg.queue}")
	private int msgQueueMaxSize = 0;
    @Value("${threadpool.msg.keepalive}")
	private int msgKeepAliveTime = 300;
    @Value("${threadpool.msg.prefix}")
	private String msgThreadNamePrefix = "msg";


    public int getMsgCoreThreadSize() {
        return msgCoreThreadSize;
    }

    public int getMsgMaxThreadSize() {
        return msgMaxThreadSize;
    }

    public int getMsgQueueMaxSize() {
        return msgQueueMaxSize;
    }

    public int getMsgKeepAliveTime() {
        return msgKeepAliveTime;
    }

    public String getMsgThreadNamePrefix() {
        return msgThreadNamePrefix;
    }
}
