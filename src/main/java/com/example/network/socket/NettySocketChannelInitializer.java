package com.example.network.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NettySocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    //nettyGameIoHandler 是无状态的，可以共享
    @Autowired
    private NettySocketIoHandler protoMsgHandler;

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("encoder", new NettyNomalEncoder());
		ch.pipeline().addLast("decoder", new NettyNomalDecoder());
		ch.pipeline().addLast("handler", protoMsgHandler);
	}

}
