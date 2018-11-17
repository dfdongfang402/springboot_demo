package com.example.demo;

import com.example.dao.ItemMapper;
import com.example.model.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wdf on 2018/9/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void testInsert() throws Exception {
        itemMapper.insert(new Item(1L, 1532675211546L, 21, 10));

        Item item = itemMapper.selectByPrimaryKey(1L);
        System.out.println("item num is " + item.getNum());
    }

}
