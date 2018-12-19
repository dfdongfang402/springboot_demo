package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@NameStyle(value = Style.normal)
public class Player implements Serializable {

    private static final long serialVersionUID = 720190397286667105L;

    @Id
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
}