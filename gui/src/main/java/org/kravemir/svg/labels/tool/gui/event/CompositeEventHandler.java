package org.kravemir.svg.labels.tool.gui.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CompositeEventHandler<T extends Event> implements EventHandler<T> {

    private final List<EventHandler<? super T>> handlers = new LinkedList<>();

    @Override
    public void handle(T event) {
        Iterator<EventHandler<? super T>> it = handlers.iterator();
        while (it.hasNext()) {
            EventHandler<? super T> handler = it.next();

            if (handler instanceof WeakEventHandler && ((WeakEventHandler<?>) handler).wasGarbageCollected()) {
                it.remove();
            } else {
                handler.handle(event);
            }
        }
    }

    public void addEventHandler(EventHandler<? super T> eventHandler) {
        handlers.add(eventHandler);
    }
}
