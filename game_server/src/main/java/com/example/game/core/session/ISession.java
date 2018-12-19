package com.example.game.core.session;

import com.example.network.Request;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.BlockingQueue;

public interface ISession {
    int getSessionId();

    Channel getChannel();

    String getAddress();

    Object getProperty(String key);

    void setProperty(String key, Object value);

    void removeProperty(String key);

    ChannelFuture writeResponse(Object object);

    String getFullIpAddress();

    boolean isTimeout();

    long getLastReadTime();

    void updateLastReadTime();

    BlockingQueue getQueue();

    void enqueue(Request request);
    void dequeue();
}
