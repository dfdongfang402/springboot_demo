package com.example.network.socket;

import com.example.network.MessageService;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class NettyNomalDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyNomalDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

	    //前四个字节是长度
		if (in.readableBytes() < 4) {
			// 不足4字节直接等待继续传数据
			return;
		}
		in.markReaderIndex();
		int packetDataLen = in.readInt();
		if (packetDataLen == 0) {
			//  如果长度为0则直接断开连接
			ctx.close();
			return;
		}
		if (in.readableBytes() < packetDataLen) {
			in.resetReaderIndex();
			return;
		}

		short cmdId = in.readShort();

        byte[] array;
        int offset;

        int readableLen= in.readableBytes();
        if (in.hasArray()) {
            array = in.array();
            offset = in.arrayOffset() + in.readerIndex();
        } else {
            array = new byte[readableLen];
            in.getBytes(in.readerIndex(), array, 0, readableLen);
            offset = 0;
        }

        //反序列化
        Message msg = MessageService.INSTANCE.getProtoMessage(cmdId, array, offset, readableLen);
        out.add(msg);

	}

}
