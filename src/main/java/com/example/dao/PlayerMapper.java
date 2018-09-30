package com.example.dao;

import com.example.model.Player;

public interface PlayerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Player record);

    int insertSelective(Player record);

    Player selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Player record);

    int updateByPrimaryKey(Player record);
}