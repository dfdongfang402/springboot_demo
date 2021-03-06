package com.example.game.core.threadpool;

import com.example.game.core.SpringContextUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wang dongfang
 * @ClassName ThreadPoolProvider.java
 * @Description TODO
 * @createTime 2018年12月14日 11:26:00
 */
public enum ThreadPoolProvider {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolProvider.class);

    // 客户端消息处理线程池
    @Getter
    private ThreadPoolExecutor cmdExecutor;
    // 异步事件处理线程池
    @Getter
    private ThreadPoolExecutor asyncEventBusExecutor;

    public void init() {
        //初始化客户端消息线程池
        ExecutorConfigData configData = SpringContextUtil.getBean(ExecutorConfigData.class);
        cmdExecutor = ThreadPoolFactory.INSTANCE.createThreadPoolExecutor(configData.getRequestExecutorCfg());
        asyncEventBusExecutor = ThreadPoolFactory.INSTANCE.createThreadPoolExecutor(configData.getAsyncEventBusExecutorCfg());
    }
}
