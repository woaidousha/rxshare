package org.bean.rxandroid.cache;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class CacheData<T> {

    private static final long STALE_MS = 100 * 1000;

    T t;

    final long timestamp;

    public CacheData(T t) {
        this.t = t;
        this.timestamp = System.currentTimeMillis();
    }

    public T get() {
        return t;
    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - timestamp < STALE_MS;
    }

}
