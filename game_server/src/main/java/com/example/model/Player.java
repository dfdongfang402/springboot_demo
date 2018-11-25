package com.example.model;

import java.io.Serializable;
import java.util.Date;

public class Player implements Serializable {
    private Long id;

    private Long userId;

    private String username;

    private Integer serverId;

    private String name;

    private String platform;

    private Date createDate;

    private Date loginDate;

    private Date logoutDate;

    private Date loginDateByDay;

    private Integer loginDays;

    private Integer multiLoginDays;

    private Long onlineTimeToday;

    private Long onlineTimeTotal;

    private Integer lv;

    private Long exp;

    private Long totalExp;

    private Integer vip;

    private Long gold;

    private Long consumeGold;

    private Long totalGold;

    private Long totalPayGold;

    private Long totalPayGiftGold;

    private String ip;

    private Long icon;

    private Long coin;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform == null ? null : platform.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
    }

    public Date getLoginDateByDay() {
        return loginDateByDay;
    }

    public void setLoginDateByDay(Date loginDateByDay) {
        this.loginDateByDay = loginDateByDay;
    }

    public Integer getLoginDays() {
        return loginDays;
    }

    public void setLoginDays(Integer loginDays) {
        this.loginDays = loginDays;
    }

    public Integer getMultiLoginDays() {
        return multiLoginDays;
    }

    public void setMultiLoginDays(Integer multiLoginDays) {
        this.multiLoginDays = multiLoginDays;
    }

    public Long getOnlineTimeToday() {
        return onlineTimeToday;
    }

    public void setOnlineTimeToday(Long onlineTimeToday) {
        this.onlineTimeToday = onlineTimeToday;
    }

    public Long getOnlineTimeTotal() {
        return onlineTimeTotal;
    }

    public void setOnlineTimeTotal(Long onlineTimeTotal) {
        this.onlineTimeTotal = onlineTimeTotal;
    }

    public Integer getLv() {
        return lv;
    }

    public void setLv(Integer lv) {
        this.lv = lv;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Long getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(Long totalExp) {
        this.totalExp = totalExp;
    }

    public Integer getVip() {
        return vip;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }

    public Long getGold() {
        return gold;
    }

    public void setGold(Long gold) {
        this.gold = gold;
    }

    public Long getConsumeGold() {
        return consumeGold;
    }

    public void setConsumeGold(Long consumeGold) {
        this.consumeGold = consumeGold;
    }

    public Long getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(Long totalGold) {
        this.totalGold = totalGold;
    }

    public Long getTotalPayGold() {
        return totalPayGold;
    }

    public void setTotalPayGold(Long totalPayGold) {
        this.totalPayGold = totalPayGold;
    }

    public Long getTotalPayGiftGold() {
        return totalPayGiftGold;
    }

    public void setTotalPayGiftGold(Long totalPayGiftGold) {
        this.totalPayGiftGold = totalPayGiftGold;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Long getIcon() {
        return icon;
    }

    public void setIcon(Long icon) {
        this.icon = icon;
    }

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }
}