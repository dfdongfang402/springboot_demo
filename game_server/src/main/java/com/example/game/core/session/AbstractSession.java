package com.example.game.core.session;

import com.example.game.core.ConfigManager;
import com.example.network.Request;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractSession implements ISession {
    private static final AtomicInteger SESSION_ID_GENERATOR = new AtomicInteger();
    private int sessionId;
    protected Map<String, Object> properties;
    private long lastReadTime;
    protected final BlockingQueue<Request> queue;
    protected int queueMaxSize = 100;

    private static int newSessionId() {
        return SESSION_ID_GENERATOR.incrementAndGet();
    }

    public AbstractSession() {
        this.properties = new ConcurrentHashMap<>();
        this.sessionId = newSessionId();
        this.lastReadTime = System.currentTimeMillis();
        this.queue = new LinkedBlockingQueue<>();
        this.queueMaxSize = ConfigManager.INSTANCE.getGameConfig().getRequestQueueMax();
    }

    public int getSessionId() {
        return sessionId;
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public void setProperty(String key, Object property) {
        this.properties.put(key, property);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    @Override
    public boolean isTimeout() {
        return System.currentTimeMillis() - lastReadTime > ConfigManager.INSTANCE.getGameConfig().getSessionTimeout() * 1000;
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    @Override
    public BlockingQueue<Request> getQueue() {
        return queue;
    }

    @Override
    public void enqueue(Request action) {
        queue.offer(action);
    }

    @Override
    public void dequeue() {
        queue.poll();
    }
}
