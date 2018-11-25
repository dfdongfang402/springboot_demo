package com.example.game.base;

import com.example.network.MessageService;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wdf on 2018/11/1.
 */
public class SocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = LoggerFactory.getLogger(SocketClientHandler.class);

    private ChannelPromise handshakeFuture;
    private Client client;

    public SocketClientHandler(Client client) {
        this.client = client;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        // System.out.println("WebSocket Client disconnected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();
        Message protoMsg = (Message) msg;
        short cmdId = MessageService.INSTANCE.getCmdIdByMsgClass(protoMsg.getClass());
        if(cmdId == 0) {
            logger.error("can't find cmdId for message {}", protoMsg.getClass().getName());
            return;
        }

        client.receive(cmdId, protoMsg);

    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }

        ctx.close();
    }
}
