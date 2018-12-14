package com.example.game.core.session;

public enum DisconnectReason {
    IDLE(0),
    KICK(1),
    BAN(2),
    UNKNOWN(3);

    private final int value;

    DisconnectReason(int id) {
        this.value = id;
    }

    public int getValue() {
        return this.value;
    }

    public static DisconnectReason getByValue(int value) {
        DisconnectReason[] dr = DisconnectReason.values();
        for(DisconnectReason pt : dr) {
            if(pt.getValue() == value) {
                return pt;
            }
        }
        return null;
    }
}
