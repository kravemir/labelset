package org.kravemir.svg.labels.tool.gui.event;

import javafx.event.Event;
import javafx.event.EventType;

public class InstanceSavedEvent extends Event {
    public static final EventType<InstanceSavedEvent> EVENT_TYPE = new EventType<>(ANY, "InstanceSavedEvent");

    private final String datasetKey;
    private final String instanceKey;

    public InstanceSavedEvent(String datasetKey, String instanceKey) {
        super(EVENT_TYPE);
        this.datasetKey = datasetKey;
        this.instanceKey = instanceKey;
    }

    public String getDatasetKey() {
        return datasetKey;
    }

    public String getInstanceKey() {
        return instanceKey;
    }
}
