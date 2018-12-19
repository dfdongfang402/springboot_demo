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

    //异步事件处理线程池配置
    @Value("${threadpool.msg.core}")
    private int eventBusCoreThreadSize = 1;
    @Value("${threadpool.msg.max}")
    private int eventBusMaxThreadSize = 1;
    @Value("${threadpool.msg.queue}")
    private int eventBusQueueMaxSize = 100;
    @Value("${threadpool.msg.keepalive}")
    private int eventBusKeepAliveTime = 300;
    @Value("${threadpool.msg.prefix}")
    private String eventBusThreadNamePrefix = "GameEvent-Bus";

    private TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;

    public ExecutorConfig getRequestExecutorCfg() {
        return new ExecutorConfig.ExecutorConfigBuilder().coreThreadSize(this.msgCoreThreadSize).maxThreadSize(this.msgMaxThreadSize)
                        .queueMaxSize(this.msgQueueMaxSize).keepAliveTime(this.msgKeepAliveTime).keepAliveTimeUnit(this.keepAliveTimeUnit)
                        .threadNamePrefix(msgThreadNamePrefix).build();
    }
    public ExecutorConfig getAsyncEventBusExecutorCfg() {
        return new ExecutorConfig.ExecutorConfigBuilder().coreThreadSize(this.eventBusCoreThreadSize).maxThreadSize(this.eventBusMaxThreadSize)
                        .queueMaxSize(this.eventBusQueueMaxSize).keepAliveTime(this.eventBusKeepAliveTime).keepAliveTimeUnit(this.keepAliveTimeUnit)
                        .threadNamePrefix(eventBusThreadNamePrefix).build();
    }
}
