package com.example.dao;

import com.example.model.Building;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BuildingMapper extends Mapper<Building> {
    @Select("select * from building where playerId = #{playerId}")
    List<Building> selectBuildingsByRoleId(long playerId);
}