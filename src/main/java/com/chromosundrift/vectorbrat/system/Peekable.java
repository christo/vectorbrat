package com.chromosundrift.vectorbrat.system;

/**
 * Function that supplies some value with semantic guarantee not to trigger side effects.
 * Does not implement supplier because it's intended to be composable with a Supplier.
 */
@FunctionalInterface
public interface Peekable<T> {

    /**
     * Implementers must return a value indicating the result of peeking at some state
     * without triggering some known side effect, e.g. checking a lazy instantiation.
     *
     * @return T the value
     */
    T peek();
}
