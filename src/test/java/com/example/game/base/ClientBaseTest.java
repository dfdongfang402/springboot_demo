package com.example.game.base;

import com.example.network.MessageService;
import com.google.protobuf.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wdf on 2018/11/1.
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class ClientBaseTest {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    protected static Client client;

    public static CountDownLatch startLatch = new CountDownLatch(1);

    @Before
    public void pre() {
        TestConfigManager.init();
        MessageService.INSTANCE.init();
        if(client == null) {
            initClient();
        }
    }

    @After
    public void post() {
        try {
            // Thread.sleep(10000);

        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void initClient() {
        new Thread(() -> {
            client = new Client();
            client.connect();
        }).start();

        try {
            startLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void write(int cmdId, Message builder) {
        client.write(cmdId, builder, 0);
    }
}
