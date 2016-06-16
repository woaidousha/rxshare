package org.bean.rxandroid;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import org.bean.rxandroid.model.User;

/**
 * Created by liuyulong@yixin.im on 2016-6-7.
 */
public class RegisterActivity extends RegisterBaseActivity {

    protected void addListener() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterButton();
            }
        };
        mUserName.addTextChangedListener(watcher);
        mPassword.addTextChangedListener(watcher);
        mAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRegisterButton();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        updateRegisterButton();
    }

    @Override
    protected void startRegister(User user) {
        RegisterTask task = new RegisterTask();
        task.execute(user);
    }

    protected void updateRegisterButton() {
        String username = mUserName.getText().toString();
        String pwd = mPassword.getText().toString();
        boolean agree = mAgree.isChecked();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd) || !agree) {
            mRegister.setEnabled(false);
        } else {
            mRegister.setEnabled(true);
        }
    }

    class RegisterTask extends AsyncTask<User, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            beforeRegister();
        }

        @Override
        protected Boolean doInBackground(User... params) {
            return actualRegister(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            afterReigster();
            String message = aBoolean ? "Register Success" : "Register Fail";
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
