package com.example.game.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wdf on 2018/11/1.
 */
public class TestConfigManager {

    private static Logger log = LoggerFactory.getLogger(Client.class);

    public static String ip;
    public static int port;

    public static void init() {
        try {
            Properties prop = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("game-test.properties");

            prop.load(stream);
            ip = prop.getProperty("ip");
            port = Integer.parseInt(prop.getProperty("port"));
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }
}
