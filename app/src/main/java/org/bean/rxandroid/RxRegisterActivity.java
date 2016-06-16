package org.bean.rxandroid;

import android.text.TextUtils;
import android.widget.Toast;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import org.bean.rxandroid.model.User;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class RxRegisterActivity extends RegisterBaseActivity {

    @Override
    protected void addListener() {

        // combineLatest 当原始的任意一个Observerable发送了数据，
        // 就会使用指定的Func去处理所有原始Observerable最近发送的数据
        Observable.combineLatest(
            RxTextView.afterTextChangeEvents(mUserName),
            RxTextView.afterTextChangeEvents(mPassword),
            RxCompoundButton.checkedChanges(mAgree),
            inputCheck()
        )
        .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mRegister.setEnabled(aBoolean);
            }
        });
        RxView.clicks(mRegister).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                register();
            }
        });
    }

    private Func3<TextViewAfterTextChangeEvent, TextViewAfterTextChangeEvent, Boolean, Boolean> inputCheck() {
        return new Func3<TextViewAfterTextChangeEvent, TextViewAfterTextChangeEvent, Boolean, Boolean>() {
            @Override
            public Boolean call(TextViewAfterTextChangeEvent event, TextViewAfterTextChangeEvent event2, Boolean aBoolean) {
                return !TextUtils.isEmpty(event.editable()) && !TextUtils.isEmpty(event2.editable()) && aBoolean;
            }
        };
    }

    @Override
    protected void startRegister(final User user) {
        Observable.just(user).map(new Func1<User, Boolean>() {
            @Override
            public Boolean call(User user) {
                return actualRegister(user);
            }
        })
        .compose(ThreadUtil.<Boolean>applySchedulers())
        .subscribe(new Subscriber<Boolean>() {

            @Override
            public void onStart() {
                beforeRegister();
            }

            @Override
            public void onCompleted() {
                afterReigster();
            }

            @Override
            public void onError(Throwable e) {
                afterReigster();
                Toast.makeText(RxRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Boolean result) {
                String message = result ? "Register Success" : "Register Fail";
                Toast.makeText(RxRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
