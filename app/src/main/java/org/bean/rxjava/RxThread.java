package org.bean.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuyulong@yixin.im on 2016-6-2.
 */
public class RxThread {

    public static void main(String args[]) {
        Observable.create(new Observable.OnSubscribe<String[]>() {
            @Override
            public void call(Subscriber<? super String[]> subscriber) {
                String[] strings = {"a", "bb", "ccc"};
                System.out.println("Create Thread:" + Thread.currentThread());
                subscriber.onNext(strings);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map(new Func1<String[], Integer[]>() {
            @Override
            public Integer[] call(String s[]) {
                System.out.println("Map Thread:" + Thread.currentThread());
                Integer[] result = new Integer[s.length];
                for (int i = 0; i < s.length; i++) {
                    result[i] = s[i].length();
                }
                return result;
            }
        })
        .concatMap(new Func1<Integer[], Observable<Integer[]>>() {
            @Override
            public Observable<Integer[]> call(Integer[] integers) {
                System.out.println("ConcatMap Thread:" + Thread.currentThread());
                return Observable.just(integers);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.newThread())
        .subscribe(new Action1<Integer[]>() {
            @Override
            public void call(Integer integer[]) {
                System.out.println("Action Thread:" + Thread.currentThread());
            }
        });
        while (true) {
        }
    }

}
