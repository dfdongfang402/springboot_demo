package com.example.game.core.session;

import com.example.game.core.IManager;
import com.example.game.exceptions.ManagerStartException;
import com.example.game.exceptions.ManagerStopException;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public enum SessionManager implements IManager {
    INSTANCE;

    private ConcurrentHashMap<Integer, ISession> sessionMap;
    private ConcurrentHashMap<Channel, ISession> sessionChannelMap;
    private AtomicLong totalSessions = new AtomicLong(0);

    public void init() {
        this.sessionMap = new ConcurrentHashMap<>();
        sessionChannelMap = new ConcurrentHashMap<>();
    }

    public void addSession(ISession session) {
        totalSessions.incrementAndGet();
        sessionMap.put(session.getSessionId(), session);
        if (session.getChannel() != null) {
            sessionChannelMap.put(session.getChannel(), session);
        }
    }

    public int getCurrentSessions() {
        return sessionChannelMap.size();
    }

    public long getTotalSessions() {
        return totalSessions.get();
    }

    public ISession removeSession(Channel channel) {
        ISession session = sessionChannelMap.get(channel);
        removeSession(session);
        return session;
    }

    public void removeSession(ISession session) {
        if (session != null) {
            this.sessionMap.remove(session.getSessionId());
            if (session.getChannel() != null) {
                this.sessionChannelMap.remove(session.getChannel());
            }
            if(session.getQueue() != null){
                session.getQueue().clear();
            }
        }
    }

    public ISession removeSession(int sessionId) {
        ISession session = sessionMap.get(sessionId);
        if (session != null) {
            removeSession(session);
        }
        return session;
    }

    @Override
    public void start() throws ManagerStartException {
    }

    @Override
    public void stop() throws ManagerStopException {

    }

    @Override
    public void restart() throws ManagerStartException,ManagerStopException {

    }

    public ISession getSession(Channel channel) {
        return sessionChannelMap.get(channel);
    }

    public boolean containsSession(ISession session) {
        return sessionMap.get(session.getSessionId()) != null;
    }

    public int getUserQueueSize(){
        int size = 0;
        for(ISession session : sessionMap.values()){
            size += session.getQueue().size();
        }
        return size;
    }

}
