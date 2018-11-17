package com.example.game.handler;

import com.example.game.core.Player;
import com.example.game.core.SpringContextUtil;
import com.example.model.Building;
import com.example.model.Item;
import com.example.network.AbstractMsgHandler;
import com.example.pb.PlayerMsg;
import com.example.pb.PlayerMsg.CPlayerLogin;
import com.example.pb.PlayerMsg.SPlayerLogin;
import com.example.service.BuildingService;
import com.example.service.ItemService;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by wdf on 2018/9/29.
 */
public class PlayerInfoHandler extends AbstractMsgHandler {
    @Override
    protected void handle(ChannelHandlerContext ctx, Player player, Message msg) {
        PlayerMsg.CPlayerLogin protoMsg = (CPlayerLogin) msg;
        System.out.println("id: " + protoMsg.getPlayerId());
        System.out.println("name: " + protoMsg.getName());

        ItemService itemService = SpringContextUtil.getBean(ItemService.class);
        List<Item> itemList = itemService.queryByPlayerId(protoMsg.getPlayerId());
        BuildingService buildingService = SpringContextUtil.getBean(BuildingService.class);
        List<Building> buildingList = buildingService.selectByPlayId(protoMsg.getPlayerId());

        SPlayerLogin.Builder retBuilder = SPlayerLogin.newBuilder();

        retBuilder.setPlayerId(1002);
        retBuilder.setName("测试完成");
        for(Item item : itemList) {
            retBuilder.addItems(itemService.toMessage(item));
        }

        for(Building building : buildingList) {
            retBuilder.addBuildings(buildingService.toMessage(building));
        }

        ctx.channel().writeAndFlush(retBuilder.build());
    }
}
