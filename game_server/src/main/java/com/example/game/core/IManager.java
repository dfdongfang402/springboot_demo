package com.example.game.core;

import com.example.game.exceptions.ManagerInitException;
import com.example.game.exceptions.ManagerStartException;
import com.example.game.exceptions.ManagerStopException;

/**
 * @author wang dongfang
 * @ClassName IManager.java
 * @Description TODO
 * @createTime 2018年12月14日 16:21:00
 */
public interface IManager {
    void init() throws ManagerInitException;

    void start() throws ManagerStartException;

    void stop() throws ManagerStopException;

    void restart() throws ManagerStartException, ManagerStopException;
}
