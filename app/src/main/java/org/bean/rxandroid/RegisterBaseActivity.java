package org.bean.rxandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import org.bean.rxandroid.model.User;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class RegisterBaseActivity extends Activity {

    EditText mUserName;
    EditText mPassword;
    CheckBox mAgree;
    Button mRegister;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        findViews();
        addListener();
    }

    protected void addListener() {

    }

    private void findViews() {
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mAgree = (CheckBox) findViewById(R.id.agree);
        mRegister = (Button) findViewById(R.id.register);

        mDialog = new ProgressDialog(this);
    }



    protected void register() {
        User user = new User();
        user.setUsername(mUserName.getText().toString());
        user.setPwd(mPassword.getText().toString());
        onRegister(user);
    }

    protected void onRegister(User user) {
    }

    protected boolean actualRegister(User user) {
        ThreadUtil.sleep(5000);
        if (user == null) {
            return false;
        }
        if (TextUtils.equals(user.getUsername(), "1")) {
            return false;
        } else if (TextUtils.equals(user.getUsername(), "2")) {
            throw new IllegalArgumentException("username error");
        }
        return true;
    }

}
