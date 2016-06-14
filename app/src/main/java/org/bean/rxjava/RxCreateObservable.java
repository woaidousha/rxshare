package org.bean.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.util.Arrays;

/**
 * Created by liuyulong@yixin.im on 2016-6-6.
 */
public class RxCreateObservable {

    public static void main(String args[]) {

        //创建Observable
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("create subscribe");
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        //创建观察者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("observer complete");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("observer: " + integer);
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onStart() {
                System.out.println("subscriber1 start");
            }

            @Override
            public void onCompleted() {
                System.out.println("subscriber1 complete");
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("subscriber1 " + integer);
            }
        };

        Action1<Integer> action = new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("action " + integer);
            }
        };

        PublishSubject<Integer> subject = PublishSubject.create();
        Action1<Integer> subjectAction = new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("SubjectAction " + integer);
            }
        };
        subject.subscribe(subjectAction);

        //订阅
        Subscription subscription = observable.subscribe(observer);
        subscription.unsubscribe();
        Util.splite();

        observable.subscribe(subscriber);
        Util.splite();

        observable.subscribe(action);
        Util.splite();

        observable.subscribe(subject);
        Util.splite();

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("create error subscribe");
                for (int i = 0; i < 5; i++) {
                    if (i == 3) {
                        Exceptions.propagate(new IllegalArgumentException("IllegalArgumentException"));
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("subscriber2 complete");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("subscriber2 error:" + throwable.toString());
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("subscriber2 " + integer);
            }
        });

        Util.splite();

        Integer[] integers = new Integer[]{4, 56, 76, 7, 4};

        Observable.from(integers).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("from action2 " + integer);
            }
        });

        Util.splite();

        Observable.just(integers).subscribe(new Action1<Integer[]>() {
            @Override
            public void call(Integer[] integers) {
                System.out.println("just action3 " + Arrays.toString(integers));
            }
        });

        Util.splite();

        Observable.just(1,2,3,4,5,6).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("just action4 " + integer);
            }
        });

        Util.splite();

        Observable.empty().subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                System.out.println("empty subscriber complete");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("empty subscriber next " + o);
            }
        });

        Util.splite();

        Observable.never().subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                System.out.println("never subscriber complete");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(Object o) {
                System.out.println("never subscriber next " + o);
            }
        });

        Util.splite();

        Observable.error(new IllegalArgumentException()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                System.out.println("error subscriber complete ");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error subscriber error " + throwable);
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

}
