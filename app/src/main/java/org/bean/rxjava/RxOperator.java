package org.bean.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyulong@yixin.im on 2016-6-6.
 */
public class RxOperator {

    public static void main(String args[]) {
        Observable<String> origin = Observable.just("a", "g", "db", "w", "o");
        //Map
        origin.map(new Func1<String, Byte>() {
            @Override
            public Byte call(String s) {
                return ((byte) s.charAt(0));
            }
        }).subscribe(new Action1<Byte>() {
            @Override
            public void call(Byte value) {
                System.out.println("map :" + value);
            }
        });

        //FlatMap
        origin.flatMap(new Func1<String, Observable<?>>() {
            @Override
            public Observable<?> call(String s) {
                if (s.equals("a")) {
                    return Observable.just(Long.MAX_VALUE);
                } else if (s.equals("db")) {
                    return Observable.just(new Integer[]{s.length()});
                } else if (s.equals("w")) {
                    return Observable.error(new IllegalArgumentException("Value is Wrong"));
                }
                return Observable.just(s);
            }
        }).onErrorReturn(new Func1<Throwable, Object>() {
            @Override
            public Object call(Throwable throwable) {
                return "error";
            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                System.out.println("flatmap complete");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("flatmap error " + throwable);
            }

            @Override
            public void onNext(Object o) {
                System.out.println("flatmap o:" + o.toString());
            }
        });

        /*
        例如在登录时候，先获取本地缓存token，再请求服务器检验token有效性
        getToken(new Callback(){
            onResult(String token) {
                checkToken(token, new Callback(){
                    onResult(boolean result) {
                        Toast result
                    }
                })
            }
        })
        getToken()
        .flatMap(
            new Func1<String, Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call(String token) {
                    return Observable.just(checkToken(token));
                }
            }
        ).subscribe(
            new Action1() {
                @Override
                public void call(Boolean reslut) {
                    Toast result
                }
            }
        );
        */

        //toSortList
        origin.toSortedList(new Func2<String, String, Integer>() {
            @Override
            public Integer call(String s, String s2) {
                return s.length() > s2.length() ? -1 : 1;
            }
        }).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                System.out.println(strings);
            }
        });

        //timeout
        Observable timeout = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String[] strings = {"a", "g", "db", "w", "o"};
                for (int i = 0; i < strings.length; i++) {
                    try {
                        Thread.sleep(500 * i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(strings[i]);
                }
                subscriber.onCompleted();
            }
        });

        timeout.timeout(100, TimeUnit.MILLISECONDS, Observable.just("time", "out")).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("time complete");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("time out error:" + throwable);
            }

            @Override
            public void onNext(String s) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("time complete " + s);
            }
        });

        //buffer
        Observable.just(1,2,3,4,5,6).buffer(4, 2).map(new Func1<List<Integer>, List<String>>() {
            @Override
            public List<String> call(List<Integer> integers) {
                System.out.println("==========map=========");
                List<String> result = new ArrayList<String>(integers.size());
                for (Integer integer : integers) {
                    result.add("integer :" + integer);
                }
                return result;
            }
        }).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                System.out.println("==========subscribe=========");
                for (String string : strings) {
                    System.out.println(string);
                }
            }
        });
    }

}
