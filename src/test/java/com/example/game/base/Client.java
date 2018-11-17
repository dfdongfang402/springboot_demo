package com.example.game.base;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wdf on 2018/11/1.
 */
public class Client {
    private SocketClient socketClient;

    private static Logger log = LoggerFactory.getLogger(Client.class);

    public Client() {
    }


    public void connect() {
        try {
            socketClient = new SocketClient(this);
            socketClient.open();
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void write(int cmdId, Message msg, int waitCmdId) {
        try {
            socketClient.send(msg);
            log.info("send: " + cmdId + " " + msg.toString());
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void receive(int cmdId, Message data) {
        log.info("receive: " + cmdId + " " + data);
        CountDownLatch cdl = ClientBaseTest.responseWaitMap.remove(cmdId);
        if(cdl != null) {
            cdl.countDown();
        }

    }

    public void receive(int cmdId, String str) {
        log.info("receive: " + cmdId + " " + str);
    }
}
