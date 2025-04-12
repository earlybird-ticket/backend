package com.earlybird.ticket.admin.application.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.util.DataUtil;
import lombok.Getter;

@Getter
public class Event<T extends EventPayload> {

    private EventType type;
    private T payload;
    private String timestamp;

    public static Event<EventPayload> of(EventType type, EventPayload payload, String timestamp) {
        Event<EventPayload> event = new Event<>();
        event.type = type;
        event.payload = payload;
        event.timestamp = timestamp;
        return event;
    }

    public static Event<EventPayload> fromJson(String json) {
        EventRaw eventRaw = DataUtil.deserialize(json, EventRaw.class);
        if (eventRaw == null) {
            return null;
        }
        Event<EventPayload> event = new Event<>();
        event.type = EventType.from(eventRaw.getType());
        event.payload = DataUtil.deserialize(eventRaw.getPayload(), event.type.getPayloadClass());
        event.timestamp = eventRaw.getTimestamp();
        return event;
    }

    public String toJson() {
        return DataUtil.serialize(this);
    }

    @Getter
    private static class EventRaw {

        private String type;
        private Object payload;
        private String timestamp;
    }
}
