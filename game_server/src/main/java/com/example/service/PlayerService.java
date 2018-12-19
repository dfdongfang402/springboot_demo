package com.example.service;

import com.example.dao.PlayerMapper;
import com.example.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wang dongfang
 * @ClassName PlayerService.java
 * @Description TODO
 * @createTime 2018年12月18日 21:27:00
 */
@Service
public class PlayerService extends BaseService<Player> {

    @Autowired
    private PlayerMapper mapper;
}
