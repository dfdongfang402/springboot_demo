package com.example.network.socket;

import com.example.network.MessageService;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyNomalEncoder extends MessageToByteEncoder<Message> {

    private static final Logger logger = LoggerFactory.getLogger(NettyNomalEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
		short cmdId = MessageService.INSTANCE.getCmdIdByMsgClass(message.getClass());
		if(cmdId == 0) {
		    logger.error("Can't find cmdId for class: " +  message.getClass().getName());
        }

        //加上2个字节协议id的长度
        out.writeInt(message.getSerializedSize() + 2);
		out.writeShort(cmdId);
		out.writeBytes(message.toByteArray());
	}
}
