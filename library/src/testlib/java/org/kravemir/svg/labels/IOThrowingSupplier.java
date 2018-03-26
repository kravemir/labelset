package org.kravemir.svg.labels;

@FunctionalInterface
public interface IOThrowingSupplier<T, E extends Exception> {
    T get() throws E;
}
