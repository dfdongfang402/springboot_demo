package com.example.network.socket;

import com.example.network.AbstractMsgHandler;
import com.example.network.MessageService;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
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
            logger.error("can't find handler for cmdid ({})", cmdId);
            return;
        }

        optionalHandler.get().handleClientRequest(protoMsg);
	}

}
