package com.example.game.subscribe;

import com.example.game.core.IManager;
import com.example.game.exceptions.ManagerInitException;
import com.example.game.exceptions.ManagerStartException;
import com.example.game.exceptions.ManagerStopException;
import com.example.redis.R;
import com.example.redis.RedisProxy;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wang dongfang
 * @ClassName SubscribeManager.java
 * @Description TODO
 * @createTime 2019年01月02日 18:07:00
 */
public enum  SubscribeManager implements IManager {

    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(SubscribeManager.class);
    private RedisProxy redisProxy;
    private JedisPubSub listener = new RedisPubSubListener();
    private static Map<String, Class> channelClasses = Maps.newHashMap();
    private boolean shutdown = false;

    static {
        channelClasses.put(HotSwapChannel.class.getSimpleName(),HotSwapChannel.class);
    }

    @Override
    public void init() throws ManagerInitException {
        redisProxy = R.Local();
    }
    @Override
    public void start() throws ManagerStartException {
        new PubSubThread("PubSubThread").start();
    }

    public Optional<ISubscribeHandler> createSubscribeHandler(String channel) {
        ISubscribeHandler handler = null;
        if(!channelClasses.containsKey(channel)) {
            return Optional.empty();
        }

        try {
            handler = (ISubscribeHandler) channelClasses.get(channel).newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            logger.error("createSubscribeHandler error" , e);
        }
        return Optional.ofNullable(handler);
    }

    @Override
    public void stop() throws ManagerStopException {
        shutdown = true;
        listener.unsubscribe();
    }

    @Override
    public void restart() throws ManagerStartException, ManagerStopException {

    }

    private class PubSubThread extends Thread {

        PubSubThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                if (shutdown) {
                    break;
                }
                try {
                    List<String> channelList = channelClasses.values().stream().map(Class::getSimpleName).collect(Collectors.toList());
                    logger.info("Pub/sub subscribe channel, {}", StringUtils.join(channelList, ","));
                    redisProxy.subscribe(listener, channelList);
                } catch (Exception e) {
                    logger.error("Pub/sub subscribe error", e);
                }
            }
        }
    }
}
