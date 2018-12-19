package com.example.network.socket;

import com.example.game.core.session.ISession;
import com.example.game.core.session.SessionManager;
import com.example.network.AbstractMsgHandler;
import com.example.network.MessageService;
import com.example.network.Request;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Sharable
public class NettySocketIoHandler extends NettyIoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyIoHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {

        Message protoMsg = (Message) message;
		short cmdId = MessageService.INSTANCE.getCmdIdByMsgClass(protoMsg.getClass());
		if(cmdId == 0) {
		    logger.error("can't find cmdId for message {}", protoMsg.getClass().getName());
		    return;
        }

        Optional<AbstractMsgHandler> optionalHandler = MessageService.INSTANCE.getMsgHandler(cmdId);
		if(!optionalHandler.isPresent()) {
            logger.error("can't find handler for cmdid {}", cmdId);
            return;
        }

        SocketChannel channel = (SocketChannel) ctx.channel();
        ISession session = SessionManager.INSTANCE.getSession(channel);
        session.updateLastReadTime();
        Request request = Request.builder().handler(optionalHandler.get()).msg(protoMsg).receiveTime(System.currentTimeMillis())
                .session(session).id(cmdId).build();
        logger.debug("receive request {}", request.toString());
        try {
            session.enqueue(request);
        } catch (Exception e) {
            logger.error("enqueue for request fail. ", e);
        }
	}

}
