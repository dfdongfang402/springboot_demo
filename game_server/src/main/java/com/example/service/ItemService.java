package com.example.service;

import com.example.dao.ItemMapper;
import com.example.model.Item;
import com.example.pb.PlayerMsg.ItemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wdf on 2018/11/15.
 */
@Service
public class ItemService extends BaseService<Item> {

    @Autowired
    private ItemMapper mapper;

    public List<Item> queryByPlayerId(long playerId) {
        // return mapper.queryByPlayerId(playerId);
        throw new RuntimeException("rrrrrr");
    }

    public ItemMessage toMessage(Item item) {
        ItemMessage.Builder builder = ItemMessage.newBuilder();
        builder.setItemId(item.getItemId());
        builder.setNum(item.getNum());

        return builder.build();
    }
}
