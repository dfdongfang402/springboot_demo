package com.example.network.socket;

import com.example.game.core.session.ISession;
import com.example.game.core.session.LinkUser;
import com.example.game.core.session.LinkUserManager;
import com.example.game.core.session.Session;
import com.example.game.core.session.SessionManager;
import com.example.game.event.EventManager;
import com.example.game.event.EventParam;
import com.example.game.event.EventType;
import com.example.game.event.IEvent;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Sharable
public class NettyIoHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyIoHandler.class);

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
                    CharsetUtil.UTF_8));  // 1
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        ISession session = new Session(channel);
        SessionManager.INSTANCE.addSession(session);
        logger.info("session created, id {}, ip {}, id: SOCKET", session.getSessionId(), channel.remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ISession session = SessionManager.INSTANCE.removeSession(ctx.channel());
        LinkUser user = LinkUserManager.INSTANCE.removeLinkUser(session);
        if (user != null) {
            Map<EventParam, Object> evtParams = Maps.newHashMap();
            evtParams.put(EventParam.USER, user);
            IEvent event = EventManager.INSTANCE.newEvent(EventType.USER_DISCONNECT, evtParams);
            EventManager.INSTANCE.postAsync(event);
        }
        logger.info("session destroyed, id {}, ip {}, id: SOCKET, queue size: {}",
                session.getSessionId(), session.getChannel().remoteAddress().toString(),session.getQueue().size());
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
        cause.printStackTrace();
        ctx.close();
	}


}
