package com.example.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by wdf on 2018/9/21.
 */

@Component
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private ServerBootstrap bootstrap;

    @Autowired
    @Qualifier("nettySocketAddress")
    private InetSocketAddress nettyPort;

    private ChannelFuture serverChannelFuture;

    public void start() throws Exception {
        logger.info("Starting server at {}", nettyPort);
        serverChannelFuture = bootstrap.bind(nettyPort).sync();
    }

    public void stop() throws Exception {
        serverChannelFuture.channel().closeFuture().sync();
    }

    public ServerBootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public InetSocketAddress getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(InetSocketAddress nettyPort) {
        this.nettyPort = nettyPort;
    }
}
