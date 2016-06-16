package org.bean.rxandroid;

import android.util.Log;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class ThreadUtil {

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void print(String msg) {
        Log.d("lyl", "=========" + Thread.currentThread() + " START ==========");
        Log.d("lyl", "||");
        Log.d("lyl", "||" + msg);
        Log.d("lyl", "||");
        Log.d("lyl", "=========" + Thread.currentThread() + " END   ==========");
    }

    public static <R> Observable<R> applySchedulers(Observable<R> o) {
        return o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <R> Observable.Transformer<R, R> applySchedulers() {
        return new Observable.Transformer<R, R>() {
            @Override
            public Observable<R> call(Observable<R> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
