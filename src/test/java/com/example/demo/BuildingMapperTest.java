package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wdf on 2018/9/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildingMapperTest {

    @Autowired
    private BuildingMapper buildingMapper;

    @Test
    public void testInsert() throws Exception {

        Building building = buildingMapper.selectByPrimaryKey(97605688293376L);

        System.out.println("building type is: " + building.getBuildingType());
    }
}
