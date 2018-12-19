package com.example.game.base;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wdf on 2018/11/1.
 */
public class TestPlayer {

    private static Logger log = LoggerFactory.getLogger(TestPlayer.class);

    public long playerId;

    public TestPlayer(long id) {
        this.playerId = id;
    }



    private void json2Message(String json, Message.Builder builder) {
        try {
            JsonFormat.parser().merge(json, builder);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

}
