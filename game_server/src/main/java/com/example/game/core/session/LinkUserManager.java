package com.example.game.core.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum LinkUserManager {

    INSTANCE;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentMap<ISession, LinkUser> LinkUsersBySession = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, LinkUser> LinkUsersById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LinkUser> LinkUsersByName = new ConcurrentHashMap<>();
    private int maxCCU = 0;


    public int getMaxCCU() {
        return maxCCU;
    }

    public void addLinkUser(LinkUser LinkUser) {
        if (this.containsId(LinkUser.getId())) {
            throw new RuntimeException("Can\'t add LinkUser: " + LinkUser.getId());
        } else {
            this.LinkUsersBySession.put(LinkUser.getSession(), LinkUser);
            this.LinkUsersById.put(LinkUser.getId(), LinkUser);
            this.LinkUsersByName.put(LinkUser.getName(), LinkUser);
            if (this.LinkUsersBySession.size() > this.maxCCU) {
                this.maxCCU = this.LinkUsersBySession.size();
            }
        }
    }

    public LinkUser getLinkUserById(int id) {
        return this.LinkUsersById.get(id);
    }

    public LinkUser getLinkUserBySession(ISession session) {
        return this.LinkUsersBySession.get(session);
    }

    public void removeLinkUser(int LinkUserId) {
        LinkUser LinkUser = this.LinkUsersById.get(LinkUserId);
        if (LinkUser == null) {
            this.logger.warn("Can\'t remove LinkUser with ID: " + LinkUserId + ". LinkUser was not found.");
        } else {
            this.removeLinkUser(LinkUser);
        }

    }

    public LinkUser removeLinkUser(ISession session) {
        LinkUser LinkUser = this.LinkUsersBySession.get(session);
        if (LinkUser != null) {
            removeLinkUser(LinkUser);
        } else {
            logger.warn("session: " + session.toString() + " doesn't login");
        }
        return LinkUser;
    }

    public void removeLinkUser(LinkUser LinkUser) {
        this.LinkUsersBySession.remove(LinkUser.getSession());
        this.LinkUsersById.remove(LinkUser.getId());
        this.LinkUsersByName.remove(LinkUser.getName());
    }

    public boolean containsId(int LinkUserId) {
        return this.LinkUsersById.containsKey(LinkUserId);
    }

    public boolean containsSessions(ISession session) {
        return this.LinkUsersBySession.containsKey(session);
    }

    public boolean containsLinkUser(LinkUser LinkUser) {
        return this.LinkUsersBySession.containsValue(LinkUser);
    }

    public List<LinkUser> getLinkUserList() {
        return new ArrayList<>(this.LinkUsersBySession.values());
    }

    public List<ISession> getAllSessions() {
        return new ArrayList<>(this.LinkUsersBySession.keySet());
    }

    public int getLinkUserCount() {
        return this.LinkUsersBySession.values().size();
    }

    public void disconnectLinkUser(ISession session) {
        LinkUser LinkUser = this.LinkUsersBySession.get(session);
        if (LinkUser == null) {
            this.logger.warn("Can\'t disconnect LinkUser with session: " + session + ". LinkUser was not found.");
        } else {
            this.disconnectLinkUser(LinkUser);
        }
    }

    public void disconnectLinkUser(LinkUser LinkUser) {
        this.removeLinkUser(LinkUser);
    }

    public LinkUser getLinkUserByName(String LinkUserName) {
        return LinkUsersByName.get(LinkUserName);
    }

    public List<LinkUser> getTimeoutLinkUsers() {
        List<LinkUser> timeoutLinkUsers = new ArrayList<>();
        List<LinkUser> allLinkUsers = new ArrayList<>(LinkUsersById.values());
        for (LinkUser LinkUser : allLinkUsers) {
            if (LinkUser != null && LinkUser.isTimeout()) {
                timeoutLinkUsers.add(LinkUser);
            }
        }
        return timeoutLinkUsers;
    }
}
