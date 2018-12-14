package com.example.network;


import com.example.game.core.session.DisconnectReason;
import com.example.game.core.session.LinkUser;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
    private static final Logger logger = LoggerFactory.getLogger(Response.class);

    public static ChannelFuture sendDisconnectResponse(DisconnectReason reason, LinkUser linkUser) {

        return linkUser.getSession().writeResponse(null);
    }
}
