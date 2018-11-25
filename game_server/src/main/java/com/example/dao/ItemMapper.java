package com.example.dao;

import com.example.model.Item;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by wdf on 2018/9/20.
 */
public interface ItemMapper extends Mapper<Item> {
    @Select("select * from item where playerId = #{playerId}")
    List<Item> queryByPlayerId(long playerId);
}
