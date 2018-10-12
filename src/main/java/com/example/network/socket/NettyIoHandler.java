package com.example.network.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

@Sharable
public class NettyIoHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyIoHandler.class);

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
                    CharsetUtil.UTF_8));  // 1
    @Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// 新连接加入
		try {
			String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		logger.info("socket新连接 registered:");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

		logger.info("socket连接 unregistered:");
	}

    @Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);  //3
        } else {
            super.userEventTriggered(ctx, evt);  //4
        }
	}

    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channel handler, exceptionCaught");
        ctx.close();
	}


}
