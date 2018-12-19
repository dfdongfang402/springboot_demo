package com.example.game.event.handler;

import com.example.controller.HelloWorldController;
import com.example.game.event.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wang dongfang
 * @ClassName LoginEventHandler.java
 * @Description TODO
 * @createTime 2018年12月19日 17:43:00
 */
public class LoginEventHandler implements IGameEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @Override
    public void handleGameEvent(IEvent event) throws Exception {
        logger.info("handle event: " + event.getType().name());
    }
}
