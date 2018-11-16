package com.example.network;

import com.example.game.core.Player;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wdf on 2018/9/29.
 */
public abstract class AbstractMsgHandler {

    private Logger logger = LoggerFactory.getLogger(AbstractMsgHandler.class);

    public void handleClientRequest(ChannelHandlerContext ctx, Message msg) {
        try {
            //TODO 获取到当前的角色对象
            Player player = new Player();
            handle(ctx, player, msg);

        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            logger.info(msg.toString());

        }

    }


    abstract protected  void handle(ChannelHandlerContext ctx,Player player, Message msg);
}
