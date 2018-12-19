package com.example.game.event;


import java.util.Map;

public class GameEvent implements IEvent {
    private final EventType eventType;
    private final Map<EventParam, Object> params;
    private final long createTime;

    public GameEvent(EventType eventType) {
        this(eventType, null);
    }

    public GameEvent(EventType eventType, Map<EventParam, Object> params) {
        this.eventType = eventType;
        this.params = params;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public Object getParameter(EventParam id) {
        Object param = null;
        if (this.params != null) {
            param = this.params.get(id);
        }

        return param;
    }

    public String toString() {
        return String.format("{id: %s, id id: %d, Params: %s }",this.eventType, this.eventType.getId(),
                this.params != null ? this.params : "none");
    }
}
