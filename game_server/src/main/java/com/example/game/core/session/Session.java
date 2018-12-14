package com.example.game.core.session;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Session extends AbstractSession {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    private SocketChannel channel;

    public Session(SocketChannel channel) {
        super();
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String getAddress() {
        return channel.remoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String getFullIpAddress() {
        InetSocketAddress socketAddress = channel.remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }

    @Override
    public ChannelFuture writeResponse(Object msg) {
        Message obj = (Message) msg;
        if(obj == null){
            logger.error("response msg is null");
            return null;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("response to session: {}, with {}", getSessionId(),msg.toString());
        }
        return this.channel.writeAndFlush(obj);
    }

    @Override
    public String toString() {
        return "id: " + getSessionId() + ", ip: " + getFullIpAddress();
    }
}
