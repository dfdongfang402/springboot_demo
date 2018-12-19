package com.example.game.core.session;

import com.example.game.core.threadpool.ThreadPoolProvider;
import com.example.network.Request;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Session extends AbstractSession {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    private SocketChannel channel;

    public Session(SocketChannel channel) {
        super();
        this.channel = channel;
    }


    @Override
    public void enqueue(Request request) {
        int size = queue.size() + 1;
        if (size > queueMaxSize) {
            logger.warn("queue size too large size {} , request {}", size,  request.toString());
            throw new RuntimeException("user queue size exceed max " + request.toString());
        }
        queue.add(request);
        synchronized (queue) {
            if (queue.size() == 1) {
                ThreadPoolProvider.INSTANCE.getCmdExecutor().execute(queue.poll());
            }
        }
    }

    @Override
    public void dequeue() {
        Request req = queue.poll();
        if (req != null) {
            ThreadPoolProvider.INSTANCE.getCmdExecutor().execute(req);
        }
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        synchronized (queue) {
            queue.clear();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String getAddress() {
        return channel.remoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String getFullIpAddress() {
        InetSocketAddress socketAddress = channel.remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }

    @Override
    public ChannelFuture writeResponse(Object msg) {
        Message obj = (Message) msg;
        if(obj == null){
            logger.error("response msg is null");
            return null;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("response to session: {}, with {}", getSessionId(),msg.toString());
        }
        return this.channel.writeAndFlush(obj);
    }

    @Override
    public String toString() {
        return "id: " + getSessionId() + ", ip: " + getFullIpAddress();
    }
}
