package com.example.model;

import java.io.Serializable;
import javax.persistence.*;

public class Building implements Serializable {
    private Long id;

    private Long playerId;

    /**
     * 建筑类型
     */
    private Short buildingType;

    private Integer x;

    private Integer y;

    /**
     * 1=未放置；2=建造中；3=正常；4=死亡
     */
    private Byte state;

    /**
     * 年龄，标识收货了几次了，主要针对果树
     */
    private Byte age;

    /**
     * 当前解锁栏位
     */
    private Byte capacity;

    /**
     * 是否翻转
     */
    private Byte isTurn;

    /**
     * 完成的时间点
     */
    private Long finishedTime;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return playerId
     */
    public Long getPlayerId() {
        return playerId;
    }

    /**
     * @param playerId
     */
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    /**
     * 获取建筑类型
     *
     * @return buildingType - 建筑类型
     */
    public Short getBuildingType() {
        return buildingType;
    }

    /**
     * 设置建筑类型
     *
     * @param buildingType 建筑类型
     */
    public void setBuildingType(Short buildingType) {
        this.buildingType = buildingType;
    }

    /**
     * @return x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @return y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * 获取1=未放置；2=建造中；3=正常；4=死亡
     *
     * @return state - 1=未放置；2=建造中；3=正常；4=死亡
     */
    public Byte getState() {
        return state;
    }

    /**
     * 设置1=未放置；2=建造中；3=正常；4=死亡
     *
     * @param state 1=未放置；2=建造中；3=正常；4=死亡
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * 获取年龄，标识收货了几次了，主要针对果树
     *
     * @return age - 年龄，标识收货了几次了，主要针对果树
     */
    public Byte getAge() {
        return age;
    }

    /**
     * 设置年龄，标识收货了几次了，主要针对果树
     *
     * @param age 年龄，标识收货了几次了，主要针对果树
     */
    public void setAge(Byte age) {
        this.age = age;
    }

    /**
     * 获取当前解锁栏位
     *
     * @return capacity - 当前解锁栏位
     */
    public Byte getCapacity() {
        return capacity;
    }

    /**
     * 设置当前解锁栏位
     *
     * @param capacity 当前解锁栏位
     */
    public void setCapacity(Byte capacity) {
        this.capacity = capacity;
    }

    /**
     * 获取是否翻转
     *
     * @return isTurn - 是否翻转
     */
    public Byte getIsTurn() {
        return isTurn;
    }

    /**
     * 设置是否翻转
     *
     * @param isTurn 是否翻转
     */
    public void setIsTurn(Byte isTurn) {
        this.isTurn = isTurn;
    }

    /**
     * 获取完成的时间点
     *
     * @return finishedTime - 完成的时间点
     */
    public Long getFinishedTime() {
        return finishedTime;
    }

    /**
     * 设置完成的时间点
     *
     * @param finishedTime 完成的时间点
     */
    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }
}