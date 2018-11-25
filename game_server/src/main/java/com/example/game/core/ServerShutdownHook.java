package com.example.game.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lifangkai on 16/1/14.
 */
public class ServerShutdownHook extends Thread {
    private Logger logger = LoggerFactory.getLogger(ServerShutdownHook.class);

    public ServerShutdownHook() {
        super("shutdown hook");
        logger.info("server shutdown hook started");
    }

    @Override
    public void run() {
        try {
            logger.error("server stop successfully");
        } catch (Throwable t) {
            logger.error("server stop error", t);
        }
    }
}
