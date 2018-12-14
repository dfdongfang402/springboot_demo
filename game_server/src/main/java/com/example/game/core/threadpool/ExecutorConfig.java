package com.example.game.core.threadpool;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author wang dongfang
 * @ClassName ExecutorConfig.java
 * @Description TODO
 * @createTime 2018年12月14日 11:19:00
 */

@Data
@Builder
public class ExecutorConfig {
    private int coreThreadSize = 16;
    private int maxThreadSize = 32;
    private int queueMaxSize = 0;
    private int keepAliveTime = 300;
    private TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
    private String threadNamePrefix = "msg";

}
