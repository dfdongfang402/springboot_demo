package com.example.game.core.session;

import com.example.game.core.ConfigManager;
import com.example.network.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Data
public class LinkUser{
    private static final String NAME_PREFIX = "Game";
    private static AtomicInteger autoIncId = new AtomicInteger(0);
    private int id;
    private String name;
    private String deviceId;
    private ISession session;
    private final Map<Object, Object> properties;

    public LinkUser(String name, ISession session) {
        this.id = autoIncId.incrementAndGet();
        if (StringUtils.isNotBlank(name)) {
            this.name = name;
        } else {
            this.name = NAME_PREFIX + id;
        }
        this.session = session;
        this.properties = new ConcurrentHashMap<>();
    }

    public void disconnect(DisconnectReason reason) {
        if(this.session == null){
            return;
        }
        this.session.setProperty("disconnectionReason", reason);
        ChannelFuture future = Response.sendDisconnectResponse(reason, this);
        if(future != null){
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    Channel channel = session.getChannel();
                    if(channel != null){
                        channel.close();
                    }
                }
            });
        }
    }

    public boolean isTimeout() {
        if (session == null) {
            return true;
        }
        return System.currentTimeMillis() - session.getLastReadTime() > ConfigManager.INSTANCE.getGameConfig().getUserTimeout();
    }

}
