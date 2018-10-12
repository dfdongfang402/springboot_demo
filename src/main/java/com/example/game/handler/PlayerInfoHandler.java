package com.example.game.handler;

import com.example.game.Player;
import com.example.network.AbstractMsgHandler;
import com.example.pb.PlayerMsg;
import com.example.pb.PlayerMsg.CPlayerLogin;
import com.google.protobuf.Message;

/**
 * Created by wdf on 2018/9/29.
 */
public class PlayerInfoHandler extends AbstractMsgHandler {
    @Override
    protected void handle(Player player, Message msg) {
        PlayerMsg.CPlayerLogin protoMsg = (CPlayerLogin) msg;
        System.out.println("id: " + protoMsg.getPlayerId());
        System.out.println("name: " + protoMsg.getName());


    }
}
