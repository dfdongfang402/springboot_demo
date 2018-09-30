package com.example.model;

import java.io.Serializable;

public class Building implements Serializable {
    private Long id;

    private Long playerId;

    private Short buildingType;

    private Integer x;

    private Integer y;

    private Byte state;

    private Byte age;

    private Byte capacity;

    private Byte isTurn;

    private Long finishedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Short getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Short buildingType) {
        this.buildingType = buildingType;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public Byte getCapacity() {
        return capacity;
    }

    public void setCapacity(Byte capacity) {
        this.capacity = capacity;
    }

    public Byte getIsTurn() {
        return isTurn;
    }

    public void setIsTurn(Byte isTurn) {
        this.isTurn = isTurn;
    }

    public Long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }
}