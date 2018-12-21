package com.example.dao;

import com.example.model.Player;
import com.example.redis.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.common.Mapper;

@CacheNamespace(implementation = MybatisRedisCache.class)
public interface PlayerMapper extends Mapper<Player> {

}