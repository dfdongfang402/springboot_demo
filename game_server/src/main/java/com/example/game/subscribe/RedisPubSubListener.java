package com.example.game.subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;


public class RedisPubSubListener extends JedisPubSub {
    private static final Logger logger = LoggerFactory.getLogger(RedisPubSubListener.class);
    @Override
    public void onMessage(String channel, String msg) {
        try {
            logger.info("receive {} from channel: {}", msg, channel);
            Optional<ISubscribeHandler> handlerOptional = SubscribeManager.INSTANCE.createSubscribeHandler(channel);
            handlerOptional.ifPresent((h) -> h.handle(msg));
        } catch(Throwable t) {
            logger.error("redis subscribe error", t);
        }
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {

    }

    @Override
    public void onSubscribe(String s, int i) {
        logger.debug("subscribe successfully");
    }

    @Override
    public void onUnsubscribe(String s, int i) {

    }

    @Override
    public void onPUnsubscribe(String s, int i) {

    }

    @Override
    public void onPSubscribe(String s, int i) {

    }
}
