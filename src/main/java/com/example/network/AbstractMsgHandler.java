package com.example.network;

import com.example.game.Player;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wdf on 2018/9/29.
 */
public abstract class AbstractMsgHandler {

    private Logger logger = LoggerFactory.getLogger(AbstractMsgHandler.class);

    public void handleClientRequest(Message msg) {
        try {
            //TODO 获取到当前的角色对象
            Player player = new Player();
            handle(player, msg);

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            logger.error(msg.toString());

        }

    }


    abstract protected  void handle(Player player, Message msg);
}
