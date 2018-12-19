package com.example.game.event;

/**
 * @author wang dongfang
 * @ClassName IEvent.java
 * @Description TODO
 * @createTime 2018年12月17日 11:20:00
 */
public interface IEvent {
    EventType getType();

    Object getParameter(EventParam param);

    long getCreateTime();
}
