package com.example.model;

/**
 * Created by wdf on 2018/9/20.
 */
public class Item {
    private long id;
    private long playerId;
    private int itemId;
    private int num;

    public Item(long id, long playerId, int itemId, int num) {
        this.id = id;
        this.playerId = playerId;
        this.itemId = itemId;
        this.num = num;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
