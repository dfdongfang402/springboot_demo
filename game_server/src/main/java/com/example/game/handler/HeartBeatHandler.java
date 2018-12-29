package com.example.game.handler;

import com.example.game.core.session.LinkUser;
import com.example.game.exceptions.GameException;
import com.example.network.AbstractMsgHandler;
import com.example.network.Request;
import com.example.pb.CommonMsg.SHeartBeat;
import com.google.protobuf.Message;

/**
 * @author wang dongfang
 * @ClassName HeartBeatHandler.java
 * @Description TODO
 * @createTime 2018年12月29日 10:13:00
 */
public class HeartBeatHandler extends AbstractMsgHandler {
    @Override
    protected Message handle(LinkUser user, Request request) throws GameException {
        return SHeartBeat.newBuilder().setTick(System.currentTimeMillis()).build();
    }

    @Override
    protected boolean inTransaction() {
        return false;
    }
}
