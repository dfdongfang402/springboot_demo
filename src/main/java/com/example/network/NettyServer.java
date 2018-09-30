package com.example.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Created by wdf on 2018/9/21.
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap b;

    @Autowired
    @Qualifier("nettySocketAddress")
    private InetSocketAddress nettyPort;

    private ChannelFuture serverChannelFuture;

    @PostConstruct
    public void start() throws Exception {
        logger.info("Starting server at {}", nettyPort);
        serverChannelFuture = b.bind(nettyPort).sync();
    }

    @PreDestroy
    public void stop() throws Exception {
        serverChannelFuture.channel().closeFuture().sync();
    }

    public ServerBootstrap getB() {
        return b;
    }

    public void setB(ServerBootstrap b) {
        this.b = b;
    }

    public InetSocketAddress getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(InetSocketAddress nettyPort) {
        this.nettyPort = nettyPort;
    }
}
