package com.example.game.core.threadpool;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wang dongfang
 * @ClassName ThreadPoolFactory.java
 * @Description TODO
 * @createTime 2018年12月14日 10:56:00
 */
public enum ThreadPoolFactory {
    INSTANCE;

    public ThreadPoolExecutor createThreadPoolExecutor(ExecutorConfig config) {
        LinkedBlockingQueue<Runnable> workQueue;
        if (config.getQueueMaxSize() > 0) {
            workQueue = new LinkedBlockingQueue<>(config.getQueueMaxSize());
        } else {
            workQueue = new LinkedBlockingQueue<>();
        }
        ThreadFactory threadFactory = new CustomThreadFactory(config.getThreadNamePrefix());
        return new ThreadPoolExecutor(config.getCoreThreadSize(), config.getMaxThreadSize(),
                config.getKeepAliveTime(), config.getKeepAliveTimeUnit(), workQueue, threadFactory);
    }


    private static class CustomThreadFactory implements ThreadFactory {

        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        private CustomThreadFactory(String prefix) {
            SecurityManager securitymanager = System.getSecurityManager();
            group = securitymanager == null ? Thread.currentThread().getThreadGroup()
                    : securitymanager.getThreadGroup();
            StringBuilder sb = new StringBuilder(32);
            if(StringUtils.isBlank(prefix)){
                sb.append("pool-").append(poolNumber.getAndIncrement());
            }else{
                sb.append(prefix);
            }
            sb.append("-thread-");
            namePrefix = sb.toString();
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(group, runnable,
                    (new StringBuilder()).append(namePrefix).append(threadNumber.getAndIncrement()).toString(), 0L);
            if (thread.isDaemon())
                thread.setDaemon(false);
            if (thread.getPriority() != 5)
                thread.setPriority(5);
            return thread;
        }
    }
}
