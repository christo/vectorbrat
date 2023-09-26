package com.chromosundrift.vectorbrat.system;

import com.google.common.base.Suppliers;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Performs lazy instantiation of a supplier with additional peekability to see if that
 * instantiation has happened. Calls to peek() return true iff the lazy initialisation has
 * happened.
 */
@ThreadSafe
public final class PeekableLazySupplier<T> implements Peekable<Boolean>, Supplier<T> {

    private final Supplier<T> delegate;
    private Boolean peeked = false;
    private final ReentrantLock lock = new ReentrantLock();

    public PeekableLazySupplier(Supplier<T> delegate) {
        this.delegate = Suppliers.memoize(delegate::get);
    }

    @Override
    public Boolean peek() {
        try {
            lock.lock();
            return peeked;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T get() {
        try {
            lock.lock();
            peeked = true;
            return delegate.get();
        } finally {
            lock.unlock();
        }
    }
}
