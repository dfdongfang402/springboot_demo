package com.example.network;

import com.example.game.core.ConfigManager;
import com.example.game.core.session.ISession;
import com.google.protobuf.Message;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Builder
public class Request<T extends Message> implements Runnable {
    private int id;
    private ISession session;
    private long receiveTime;
    private long handleStartTime;
    private T msg;
    private AbstractMsgHandler handler;
    private static Logger logger = LoggerFactory.getLogger(Request.class);

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            handler.handleClientRequest(this);
            long end = System.currentTimeMillis();
            long totalTime = end - receiveTime;
            if (totalTime >= ConfigManager.INSTANCE.getGameConfig().getRequestSlowMillis()) {
                long handleTime = end - start;
                logger.info("[SLOW_REQ] WaitTime: {} HandleTime: {} TotalTime: {} REQ: {}"
                        , totalTime - handleTime, handleTime, totalTime, this.toString());
            }
        } catch (Throwable e) {
            logger.error("run action execute exception. action : " + this.toString(), e);
        } finally {
            if (session.getQueue() != null) {
                session.dequeue();
            }
        }
    }

    public String stat() {
        return String.format("wait cost time %d, handle cost time %d", (handleStartTime - receiveTime), (System.currentTimeMillis() - handleStartTime));
    }
}
