package com.example.network.socket;

import com.example.network.MessageService;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyNomalEncoder extends MessageToByteEncoder<Message> {


	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
		short cmdId = MessageService.INSTANCE.getCmdIdByMsgClass(message.getClass());
		if(cmdId == 0) {
		    throw new RuntimeException("Can't find cmdId for class: " +  message.getClass().getName());
        }

        out.writeInt(message.getSerializedSize());
		out.writeBytes(message.toByteArray());
	}
}
