package com.example.game.handler;

import com.example.game.core.SpringContextUtil;
import com.example.game.core.session.LinkUser;
import com.example.game.core.session.LinkUserManager;
import com.example.game.event.EventManager;
import com.example.game.event.EventType;
import com.example.game.event.GameEvent;
import com.example.game.event.EventParam;
import com.example.game.event.IEvent;
import com.example.game.exceptions.GameException;
import com.example.game.exceptions.GameExceptionCode;
import com.example.model.Player;
import com.example.network.AbstractMsgHandler;
import com.example.network.Request;
import com.example.pb.PlayerMsg;
import com.example.pb.PlayerMsg.CPlayerLogin;
import com.example.pb.PlayerMsg.SPlayerLogin;
import com.example.service.PlayerService;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;

import java.util.Map;

/**
 * Created by wdf on 2018/9/29.
 */
public class PlayerLoginHandler extends AbstractMsgHandler {
    @Override
    protected Message handle(LinkUser user, Request request) throws GameException {
        PlayerMsg.CPlayerLogin protoMsg = (CPlayerLogin) request.getMsg();
        System.out.println("id: " + protoMsg.getPlayerId());
        System.out.println("name: " + protoMsg.getName());

        PlayerService playerService = SpringContextUtil.getBean(PlayerService.class);
        Player player = playerService.selectByPrimaryKey(protoMsg.getPlayerId());
        Player player2 = playerService.selectByPrimaryKey(protoMsg.getPlayerId());
        if(player == null) {
            throw new GameException(GameExceptionCode.INVALID_OPT, "player is not exist");
        }

        //登录成功初始化Linkuser
        if(user != null) {
            throw new GameException(GameExceptionCode.INVALID_OPT, "user is  exist");
        }
        user = new LinkUser(protoMsg.getName(), request.getSession());
        user.setPlayerId(player.getId());
        LinkUserManager.INSTANCE.addLinkUser(user);

        //登录成功事件
        Map<EventParam, Object> evtParams = Maps.newHashMap();
        evtParams.put(EventParam.USER, user);
        final IEvent loginEvent = EventManager.INSTANCE.newEvent(EventType.USER_LOGIN, evtParams);
        EventManager.INSTANCE.postAsync(loginEvent);

        //返回消息
        SPlayerLogin.Builder retBuilder = SPlayerLogin.newBuilder();

        retBuilder.setPlayerId(player.getId());
        retBuilder.setName(player.getName());

        return retBuilder.build();
    }
}
