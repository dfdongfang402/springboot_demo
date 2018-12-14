package com.example.game.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wang dongfang
 * @ClassName ConfigManager.java
 * @Description TODO
 * @createTime 2018年12月14日 15:58:00
 */
public enum ConfigManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private GameConfig gameConfig = SpringContextUtil.getBean(GameConfig.class);

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
