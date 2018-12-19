package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by wdf on 2018/9/20.
 */
@Data
@NoArgsConstructor
public class Item {
    private Long id;
    private Long playerId;
    private Integer itemId;
    private Integer num;

    public Item(long id, long playerId, int itemId, int num) {
        this.id = id;
        this.playerId = playerId;
        this.itemId = itemId;
        this.num = num;
    }
}
