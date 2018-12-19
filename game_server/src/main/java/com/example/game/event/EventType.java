package com.example.game.event;

/**
 * @author wang dongfang
 * @ClassName EventType.java
 * @Description TODO
 * @createTime 2018年12月17日 11:20:00
 */
public enum EventType {
    HANDSHAKE(0),
    USER_LOGIN(1),
    USER_LOGOUT(2),
    USER_DISCONNECT(3),
    PINGPONG(99);

    private int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EventType fromId(int id) {
        for (EventType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
