package com.example.game.player;

import com.example.game.core.ConfigManager;
import com.example.game.core.SpringContextUtil;
import com.example.model.Player;
import com.example.service.PlayerService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author wang dongfang
 * @ClassName PlayerManager.java
 * @Description TODO
 * @createTime 2018年12月29日 11:35:00
 */
public enum PlayerManager {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    private LoadingCache<Long, Player> playerCache;

    PlayerManager() {
        playerCache = CacheBuilder.newBuilder()
            .maximumSize(ConfigManager.INSTANCE.getGameConfig().getPlayerCacheMax())
            .expireAfterAccess(ConfigManager.INSTANCE.getGameConfig().getPlayerCacheExpireAfterAccess(), TimeUnit.MINUTES)
            .expireAfterWrite(ConfigManager.INSTANCE.getGameConfig().getPlayerCacheExpireAfterWrite(), TimeUnit.MINUTES)
            .removalListener((RemovalNotification<Long, Player> notification) -> {
                Player player = notification.getValue();
                if (player != null) {
                    logger.info("player has removed. uid {}, name {}", notification.getKey(), player.getName());
                }
            })
            .recordStats()
            .build(new CacheLoader<Long, Player>() {
                @Override
                public Player load(Long playerId) {
                    try {
                        PlayerService playerService = SpringContextUtil.getBean(PlayerService.class);
                        return playerService.selectByPrimaryKey(playerId);
                    } catch (Exception e) {
                        logger.error("load player error, id={}, exception={}", playerId, e.getMessage());
                        return null;
                    }
                }
            });
    }


    /**
     * 获取在线用户
     *
     * @param playerId
     * @return
     */
    public Player getPresentPlayer(long playerId) {
        if (playerCache == null) return null;
        return playerCache.getIfPresent(playerId);
    }

    public Player getPlayer(long playerId) {
        Player userProfile = null;
        try {
            userProfile = playerCache.get(playerId);
        } catch (ExecutionException e) {
            e.printStackTrace();
            logger.error("error" , e);
        }
        return userProfile;
    }

    public void addPlayerToCache(Player player) {
        playerCache.put(player.getId(), player);
    }

    public void removePlayer(long playerId) {
        playerCache.invalidate(playerId);
    }
}
