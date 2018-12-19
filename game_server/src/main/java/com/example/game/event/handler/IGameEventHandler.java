package com.example.game.event.handler;

import com.example.game.event.IEvent;

/**
 * @author wang dongfang
 * @ClassName IGameEventHandler.java
 * @Description TODO
 * @createTime 2018年12月19日 17:41:00
 */
public interface IGameEventHandler {
    void handleGameEvent(IEvent event) throws Exception;
}
