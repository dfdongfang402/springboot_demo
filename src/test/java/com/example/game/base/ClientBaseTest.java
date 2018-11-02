package com.example.game.base;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wdf on 2018/11/1.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientBaseTest {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    public static String serverUrl ;

    @Before
    public void pre() {
        TestConfigManager.init();
        serverUrl = TestConfigManager.url;
    }

    @After
    public void post() {
        try {
            // Thread.sleep(10000);

        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

}
