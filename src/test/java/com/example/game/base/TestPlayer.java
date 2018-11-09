package com.example.game.base;

import com.example.game.core.Player;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wdf on 2018/11/1.
 */
public class TestPlayer {

    private static Logger log = LoggerFactory.getLogger(TestPlayer.class);

    private Client client;

    public Player player;

    public long playerId;

    public TestPlayer(long id) {
        this.playerId = id;
        login(id);
    }

    public void login(long playerId) {
        player = new Player();

        client = new Client();

        client.connect();

    }

    private void json2Message(String json, Message.Builder builder) {
        try {
            JsonFormat.parser().merge(json, builder);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void write(int cmdId, Message builder) {
        client.write(cmdId, builder, 0);
    }

}
