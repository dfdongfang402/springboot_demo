package com.example.dao;

import com.example.model.Item;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by wdf on 2018/9/20.
 */
public interface ItemMapper {
    @Select("select * from item")
    public List<Item> queryAll();

    @Select("select * from item where id = #{id}")
    public Item queryById(long id);

    @Insert("INSERT INTO item(id, playerId, itemId, num) VALUES(#{id}, #{playerId}, #{itemId}, #{num})")
    public int insert(Item item);

    @Delete("DELETE FROM item WHERE id = #{id}")
    public int delete(long id);

    @Update("UPDATE item SET playerId=#{playerId}, itemId=#{itemId}, num=#{num} WHERE id =#{id}")
    public int update(Item item);
}
