package com.example.game;

import com.example.game.base.ClientBaseTest;
import com.example.game.base.TestPlayer;
import com.example.pb.PlayerMsg.CPlayerLogin;
import org.junit.Test;

/**
 * Created by wdf on 2018/11/1.
 */

public class TestLogin extends ClientBaseTest {

    @Test
    public void testLogin() {
        TestPlayer tp = new TestPlayer(1001);
        CPlayerLogin msg = CPlayerLogin.newBuilder().setPlayerId(tp.playerId).setName("测试完成").build();
        write(1001, msg, 1002);
    }
}
