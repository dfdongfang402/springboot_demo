package com.example.game.base;

import com.example.network.socket.NettyNomalDecoder;
import com.example.network.socket.NettyNomalEncoder;
import com.example.pb.PlayerMsg.CPlayerLogin;
import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by wdf on 2018/11/1.
 */
public class SocketClient {
    private Channel ch;
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Client client;

    public SocketClient(Client client) {
        this.client = client;
    }

    public void open() throws Exception {

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("initChannel");
                            ch.pipeline().addLast("encoder", new NettyNomalEncoder())
                                    .addLast("decoder", new NettyNomalDecoder())
                                    .addLast("handler", new SocketClientHandler(client));
                        }
                    });
            ChannelFuture future = bootstrap.connect(TestConfigManager.ip, TestConfigManager.port);
            ch = future.sync().channel();
            System.out.println("begin");

            CPlayerLogin msg = CPlayerLogin.newBuilder().setPlayerId(10).setName("测试").build();
            ch.writeAndFlush(msg);

            future.channel().closeFuture().sync();
            System.out.println("Closed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    public void close() throws InterruptedException {
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
    }

    public void eval(final String text) throws IOException {
        ch.writeAndFlush(new TextWebSocketFrame(text));
    }

    public void send(Message msg) {
        ch.writeAndFlush(msg);
    }
}
