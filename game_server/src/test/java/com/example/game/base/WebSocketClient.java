package com.example.game.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
public class WebSocketClient {
    private final URI uri;
    private Channel ch;
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Client client;

    public WebSocketClient(final String uri, Client client) {
        this.uri = URI.create(uri);
        this.client = client;
    }

    public void open() throws Exception {
        Bootstrap b = new Bootstrap();
        String protocol = uri.getScheme();
        final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory
                .newHandshaker(uri, WebSocketVersion.V13, null, false, HttpHeaders.EMPTY_HEADERS, 1280000), client);

        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

                final SslContext sslCtx;
                sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
                if (uri.toString().startsWith("wss")) {
                    ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), "127.0.0.1", 9944));
                }

                ch.pipeline().addLast("http-codec", new HttpClientCodec());
                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                ch.pipeline().addLast("ws-handler", handler);
            }
        });
        ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
    }

    public void close() throws InterruptedException {
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
    }

    public void eval(final String text) throws IOException {
        ch.writeAndFlush(new TextWebSocketFrame(text));
    }
}
