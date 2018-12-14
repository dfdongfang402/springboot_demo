package com.example.network;

import com.example.game.core.session.ISession;
import com.google.protobuf.Message;

public class Request {
    private Type type;
    private short id;
    private ISession session;
    private long receiveTime;
    private long handleStartTime;

    public enum Type {
        EVENT, GAME;

        public static Type fromIndex(int index) {
            Type[] values;
            int len = (values = values()).length;

            if (index >= 0 && index < len) {
                return values[index];
            }
            throw new IllegalArgumentException("Request Type index " + index);
        }
    }

    public Request(ISession session) {
        this.session = session;
        this.receiveTime = System.currentTimeMillis();
    }

    public Request(ISession session, Message msg) {
        receiveTime = System.currentTimeMillis();
        this.session = session;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public short getId() {
        return id;
    }

    public ISession getSession() {
        return session;
    }

    public void setHandleStartTime(long handleStartTime) {
        this.handleStartTime = handleStartTime;
    }

    public boolean isGameQuest() {
        return type == Type.GAME;
    }

    public String stat() {
        return String.format("wait cost time %d, handle cost time %d", (handleStartTime - receiveTime), (System.currentTimeMillis() - handleStartTime));
    }

    @Override
    public String toString() {
        return String.format("session id: %s send {type: %s, id: %d, p: %s}", session.getSessionId(), type.toString(), id);
    }
}
