package org.bean.rxandroid.cache;

import android.content.Context;
import rx.Observable;

/**
 * Created by liuyulong@yixin.im on 2016-6-8.
 */
public abstract class DiskLevelCache<K, T> extends Cache<K, T> {

    public DiskLevelCache(Context context) {
        super(context);
    }

    @Override
    protected Observable<T> combination(K key) {
        return Observable.concat(memory(key), disk(key));
    }

    @Override
    protected T requestFromNetwork(K key) {
        return null;
    }
}
