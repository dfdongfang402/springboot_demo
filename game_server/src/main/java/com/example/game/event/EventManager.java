package com.example.game.event;

import com.example.game.core.IManager;
import com.example.game.core.threadpool.ThreadPoolProvider;
import com.example.game.event.handler.IGameEventHandler;
import com.example.game.event.handler.LoginEventHandler;
import com.example.game.exceptions.ManagerInitException;
import com.example.game.exceptions.ManagerStartException;
import com.example.game.exceptions.ManagerStopException;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wang dongfang
 * @ClassName EventManager.java
 * @Description TODO
 * @createTime 2018年12月17日 10:57:00
 */
public enum EventManager implements IManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(EventManager.class);

    private EventBus eventBus;
    private EventBus asyncEventBus;

    private final Map<Integer, IGameEventHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public void init() throws ManagerInitException {
        eventBus = new EventBus("SyncBus");
        asyncEventBus = new AsyncEventBus("AsyncBus", ThreadPoolProvider.INSTANCE.getAsyncEventBusExecutor());
    }

    @Override
    public void start() throws ManagerStartException {
        registeListener(this);
        registeHandler();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleServerEvent(IEvent event) {
        long startTime = System.currentTimeMillis();

        int handlerId = event.getType().getId();
        try {
            IGameEventHandler handler = handlers.get(handlerId);
            if (handler == null) {
                logger.error("Event handler not found id: {}, name {}" , handlerId ,event.getType().name());
                return;
            }
            handler.handleGameEvent(event);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - event.getCreateTime();
            if(totalTime > 1000){
                long handleTime = endTime - startTime;
                logger.info("[SLOW_EVENT] WaitTime: {} HandleTime: {} TotalTime: {} EVENT: {}"
                        ,totalTime - handleTime, handleTime, totalTime, event.toString());
            }
        } catch (InstantiationException var4) {
            logger.warn("Cannot instantiate handler class: ", var4);
        } catch (IllegalAccessException var5) {
            logger.warn("Illegal access for handler class: ", var5);
        } catch (Exception e) {
            logger.error("handle event error: ", e);
        }
    }

    public void addHandler(int handlerId, IGameEventHandler handler) {
        this.handlers.put(handlerId, handler);
    }

    public void clearAll() {
        this.handlers.clear();
    }

    public void removeHandler(int handlerId) {
        this.handlers.remove(handlerId);
    }

    @Override
    public void stop() throws ManagerStopException {

    }

    @Override
    public void restart() throws ManagerStartException, ManagerStopException {

    }

    public void postAsync(IEvent event) {
        asyncEventBus.post(event);
    }

    private void registeListener(Object listener) {
        eventBus.register(listener);
        asyncEventBus.register(listener);
    }

    private void registeHandler() {
        addHandler(EventType.USER_LOGIN.getId(), new LoginEventHandler());
    }

    public IEvent newEvent(EventType eventType, Map<EventParam, Object> evtParams) {
        return new GameEvent(eventType, evtParams);
    }
}
