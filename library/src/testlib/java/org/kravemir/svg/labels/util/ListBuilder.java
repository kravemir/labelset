package org.kravemir.svg.labels.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListBuilder<T> {

    private ArrayList<T> list;

    public ListBuilder() {
        list = new ArrayList<>();
    }

    public ListBuilder(int capacity) {
        list = new ArrayList<>(capacity);
    }

    public ListBuilder<T> add(T o) {
        list.add(o);
        return this;
    }

    public ListBuilder<T> add(int count, T o) {
        for(int i = 0; i < count; i++) {
            list.add(o);
        }
        return this;
    }

    public void clear() {
        list.clear();
    }

    public List<T> build() {
        return Collections.unmodifiableList(list);
    }
}
