package org.bean.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyulong@yixin.im on 2016-6-3.
 */
public class RxDownload {

    static class DownloadState {
        PublishSubject subject;
        Subscription subscription;
    }

    public static void main(String args[]) {
        final String urls[] = {
                "http://nos-yx.netease.com/yxgame/0256d78df30b4034bac0e26d426ad948.apk?download=L10.sdk18.signed.iconed_yixin_nopay_L10_7_20160511_170425.apk",
                " http://nos-yx.netease.com/yxgame/95dc9c5e2c0c46699294ba04f74cbe0d.apk?download=ChronoBlade_cn_update2_android_review_yixin_nopay_ma8_8_20160219_135805.apk"
        };
        final Action1<Map.Entry<String, Integer>> action1 = new Action1<Map.Entry<String, Integer>>() {
            @Override
            public void call(Map.Entry<String, Integer> entry) {
                System.out.println("1 Progress Thread:" + Thread.currentThread() + ", " + entry.toString());
            }
        };

        final Map<String, DownloadState> subscriptions = new HashMap<>();

        for (String url : urls) {
            PublishSubject<Map.Entry<String, Integer>> subject = PublishSubject.create();
            Subscription s = createDownload(url).subscribe(subject);
            DownloadState state = new DownloadState();
            state.subject = subject;
            state.subscription = s;
            subscriptions.put(url.hashCode() + "", state);
        }

        final Subscription observer1 = subscriptions.get(String.valueOf(urls[0].hashCode())).subject.subscribe(action1);
        Subscription observer2 = subscriptions.get(String.valueOf(urls[1].hashCode())).subject.subscribe(action1);
        System.out.println("observer1:" + observer1.toString());
        System.out.println("observer2:" + observer2.toString());

        Observable.timer(1, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                //停止观测第一个下载
                if (!observer1.isUnsubscribed()) {
                    System.out.println("停止观测第一个下载");
                    observer1.unsubscribe();
                }
            }
        });

        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                //暂停第一个下载
                String key = String.valueOf(urls[0].hashCode());
                Subscription s = subscriptions.get(key).subscription;
                if (!s.isUnsubscribed()) {
                    System.out.println("停止第一个下载");
                    s.unsubscribe();
                }
            }
        });

        while (true) {
        }
    }

    private static Observable<Map.Entry<String, Integer>> createDownload(final String url) {
        System.out.println("Start Download Thread" + Thread.currentThread());
        return Observable.create(new Observable.OnSubscribe<Map.Entry<String, Integer>>() {
            @Override
            public void call(Subscriber<? super Map.Entry<String, Integer>> subscriber) {
                System.out.println(url.hashCode() + " subscriber: " + subscriber.toString());
                Map.Entry<String, Integer> entry = new AbstractMap.SimpleEntry<>(String.valueOf(url.hashCode()), 0);
                URLConnection connection = null;
                InputStream inputStream = null;
                RandomAccessFile randomAccessFile = null;
                try {
                    System.out.println(url);
                    URL url1 = new URL(url);
                    connection = url1.openConnection();
                    inputStream = connection.getInputStream();
                    File file = new File("E:\\Temp", url.hashCode() + ".apk");
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    int length = connection.getContentLength();
                    int count = 0;
                    int offset = 0;
                    byte[] buffer = new byte[1024 * 10];
                    int percent = 0;
                    while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
                        randomAccessFile.seek(offset);
                        randomAccessFile.write(buffer, 0, count);
                        offset += count;
                        int newpercent = (int) (1.0f * offset / length * 100);
                        if (percent != newpercent) {
                            percent = newpercent;
                            entry.setValue(percent);
                            subscriber.onNext(entry);
                        }
                        if (subscriber.isUnsubscribed()) {
                            subscriber.onError(null);
                            return;
                        }
                    }
                    subscriber.onCompleted();
                    System.out.println(url.hashCode() + " end");
                } catch (IOException e) {
                    Exceptions.propagate(e);
                } finally {
                    close(randomAccessFile);
                    close(inputStream);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
