package com.example.game.core.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfigData {

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

    private TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;

    public ExecutorConfig getRequestExecutorCfg() {
        return new ExecutorConfig.ExecutorConfigBuilder().coreThreadSize(this.msgCoreThreadSize).maxThreadSize(this.msgMaxThreadSize)
                        .queueMaxSize(this.msgQueueMaxSize).keepAliveTime(this.msgKeepAliveTime).keepAliveTimeUnit(this.keepAliveTimeUnit)
                        .threadNamePrefix(msgThreadNamePrefix).build();
    }
}
