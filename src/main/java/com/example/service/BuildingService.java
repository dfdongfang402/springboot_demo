package com.example.service;

import com.example.dao.BuildingMapper;
import com.example.model.Building;
import com.example.pb.PlayerMsg.BuildingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wdf on 2018/11/15.
 */

@Service
public class BuildingService extends BaseService<Building> {
    @Autowired
    private BuildingMapper mapper;

    public List<Building> selectByPlayId(long playerId) {
        return mapper.selectBuildingsByRoleId(playerId);
    }

    public BuildingMessage toMessage(Building building) {
        BuildingMessage.Builder builder = BuildingMessage.newBuilder();
        builder.setBuildingType(building.getBuildingType());
        builder.setId(builder.getId());

        return builder.build();
    }
}
