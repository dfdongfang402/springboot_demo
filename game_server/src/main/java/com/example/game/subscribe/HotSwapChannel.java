package com.example.game.subscribe;

import com.example.game.hotswap.HotSwapAgentManager;

/**
 * @author wang dongfang
 * @ClassName HotSwapChannel.java
 * @Description TODO
 * @createTime 2019年01月02日 18:27:00
 */
public class HotSwapChannel implements ISubscribeHandler {
    @Override
    public void handle(String msg) {
        HotSwapAgentManager.reloadAgent();
    }
}
