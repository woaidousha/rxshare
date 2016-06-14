package org.bean.rxandroid.cache;

import android.content.Context;
import org.bean.rxandroid.ThreadUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public abstract class Cache<K, T> {

    private File mCacheDir;

    public Cache(Context context) {
        mCacheDir = context.getCacheDir();
    }

    protected abstract String getType();

    protected Map<K, CacheData<T>> memory = new HashMap<>();

    private static AppsCache APPS_CACHE;

    public static AppsCache appsCache(Context context) {
        if (APPS_CACHE == null) {
            APPS_CACHE = new AppsCache(context);
        }
        return APPS_CACHE;
    }

    protected Observable<T> combination(K key) {
        return Observable.concat(memory(key), disk(key), network(key));
    }

    public Observable<T> query(K key) {
        return combination(key).first(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return t != null;
            }
        });
    }

    public Observable<T> memory(final K key) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(readFromMemory(key));
                subscriber.onCompleted();
            }
        });

        return observable.doOnNext(log("MEMORY key:" + key));
    }

    public Observable<T> disk(final K key) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T t = readFromDisk(key);
                subscriber.onNext(t);
                subscriber.onCompleted();
            }
        });

        // 缓存磁盘数据到内存
        return observable.doOnNext(new Action1<T>() {
            @Override
            public void call(T data) {
                writeToMemory(key, data);
            }
        }).doOnNext(log("DISK key:" + key));
    }

    public Observable<T> network(final K key) {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T t = requestFromNetwork(key);
                subscriber.onNext(t);
                subscriber.onCompleted();
            }
        });

        // 缓存网络数据到磁盘和内存
        return observable.doOnNext(new Action1<T>() {
            @Override
            public void call(T data) {
                writeToDisk(key, data);
                writeToMemory(key, data);
            }
        }).doOnNext(log("NETWORK key:" + key));
    }

    protected T readFromMemory(K key) {
        CacheData<T> cacheData = memory.get(key);
        T t = null;
        if (cacheData != null && cacheData.isUpToDate()) {
            t = cacheData.get();
        }
        return t;
    }

    protected void writeToMemory(K key, T t) {
        CacheData<T> cacheData = new CacheData<>(t);
        memory.put(key, cacheData);
    }

    protected void writeToDisk(K key, T t) {
    }
    protected abstract T readFromDisk(K key);

    protected abstract T requestFromNetwork(K key);

    private String getDiskCachePath(K key) {
        return mCacheDir.getPath() + "/" + getType() + "/" + key.hashCode();
    }

    Action1<T> log(final String source) {
        return new Action1<T>() {
            @Override
            public void call(T data) {
                if (data == null) {
                    ThreadUtil.print(source + " does not have any data.");
                } else {
                    ThreadUtil.print(source + " has the data you are looking for!");
                }
            }
        };
    }
}
