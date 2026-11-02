package xyz.refinedev.phoenix.utils.repository;

import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.lib.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class KeyedLockCache<K> {

    private final LoadingCache<K, ReentrantLock> locks;

    private static final long time = 5L;

    public KeyedLockCache() {
        this.locks =
                Caffeine.newBuilder()
                        .expireAfterAccess(time, TimeUnit.MINUTES)
                        .build(result -> new ReentrantLock());
    }

    public void withLock(K k, Runnable runnable) {
        ReentrantLock reentrantLock;
        (reentrantLock = (ReentrantLock) this.locks.get(k)).lock();
        try {
            runnable.run();
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean isLocked(K k) {
        ReentrantLock reentrantLock;
        return (reentrantLock = (ReentrantLock) this.locks.getIfPresent(k)) != null
                && reentrantLock.isLocked();
    }
}
