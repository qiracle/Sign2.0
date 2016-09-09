package com.moz.signin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.moz.signin.bordercast.NetWorkChangeReceiver;
import com.moz.signin.services.ServiceRulesException;
import com.moz.signin.services.UserService;
import com.moz.signin.services.UserServiceImpl;

import org.apache.http.conn.ConnectTimeoutException;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;



public class LoginActivity extends AppCompatActivity {


    private IntentFilter intentFilter;
    private UserService userService = new UserServiceImpl();
    private RadioGroup radioGroup;
    private NetWorkChangeReceiver netWorkChangeReceiver = new NetWorkChangeReceiver();
    // UI references.
    private EditText UserId;
    private EditText UserPwd;
    private View ProgressView;
    private View LoginFormView;
    private Drawable mIconPerson;
    private Drawable mIconLock;
    private CheckBox RememberPasswordCheck;
    private SharedPreferences sharedPreferences;
    String userName;
    private static final int FLAG_LOGIN_SUCCESS = 1;

    private static final String MSG_LOGIN_ERROR = "登录出错";
    private static final String MSG_LOGIN_SUCCESS = "登录成功";
    //public static final String MSG_LOGIN_FAILED = "登录名|登录密码出错。";
    public static final String MSG_SERVER_ERROR = "请求服务器错误";
    public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时";
    public static final String MSG_RESPONSE_TIMEOUT = "服务器响应超时";

    private void readPwdToXml() {
        // 从config.xml里读取学号密码
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        RememberPasswordCheck = (CheckBox) findViewById(R.id.remember_password_check);
        String name = sharedPreferences.getString("userid", "");
        String pwd = sharedPreferences.getString("userpwd", "");
        String isChecked = sharedPreferences.getString("check", "false");/*如果没有找到则以第二个值作为默认*/
        UserId = (EditText) findViewById(R.id.login_user_et);
        UserPwd = (EditText) findViewById(R.id.login_password_et);
        RememberPasswordCheck = (CheckBox) findViewById(R.id.remember_password_check);
        UserId.setText(name);
        UserPwd.setText(pwd);
        if ("true".equals(isChecked)) {
            RememberPasswordCheck.setChecked(true);
        }

    }

    private void writePwdToXml(String userId, String userPwd) {
        // sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        RememberPasswordCheck = (CheckBox) findViewById(R.id.remember_password_check);
        Editor edit = sharedPreferences.edit();
        if (RememberPasswordCheck.isChecked()) {

            edit.putString("userid", userId);
            edit.putString("userpwd", userPwd);
            edit.putString("check", "true");
            edit.commit();

        } else {

            edit.putString("userid", "");
            edit.putString("userpwd", "");
            edit.putString("check", "false");
            edit.commit();
        }

    }

    private void netWork() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        LoginActivity.this.registerReceiver(netWorkChangeReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        netWork();
        // Set up the login form.
        readPwdToXml();
        mIconPerson = getResources().getDrawable(R.drawable.txt_person_icon);
        mIconPerson.setBounds(5, 1, 60, 50);
        mIconLock = getResources().getDrawable(R.drawable.txt_lock_icon);
        mIconLock.setBounds(5, 1, 60, 50);
        UserId = (EditText) findViewById(R.id.login_user_et);
        UserPwd = (EditText) findViewById(R.id.login_password_et);


        UserId.setCompoundDrawables(mIconPerson, null, null, null);
        UserPwd.setCompoundDrawables(mIconLock, null, null, null);

        LoginFormView = findViewById(R.id.login_form);
        ProgressView = findViewById(R.id.login_progress);
        Button logInButton = (Button) findViewById(R.id.login_login);
        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netWorkChangeReceiver);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        UserId.setError(null);
        UserPwd.setError(null);

        // Store values at the time of the login attempt.
        final String userId = UserId.getText().toString().trim();
        final String userPwd = UserPwd.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(userPwd) || !isPasswordValid(userPwd)) {
            UserPwd.setError(getString(R.string.error_invalid_password));
            focusView = UserPwd;
            cancel = true;
        }


        // Check for a valid name address.
        if (TextUtils.isEmpty(userId)) {
            UserId.setError(getString(R.string.error_field_required));
            focusView = UserId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            showProgress(true);
            int identity = getIdentity();
            login(userId, userPwd, identity);

        }

    }

    private void login(final String userId, final String userPwd, final int identity) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {


                    boolean state = userService.userLogin(userId, userPwd, identity);
                    if (state) {


                        userName = userService.getName(userId, identity);
                        handler.sendEmptyMessage(FLAG_LOGIN_SUCCESS);
                        writePwdToXml(userId, userPwd);
                        Intent intent = new Intent();
                        switch (identity) {
                            case 1:
                                LoginActivity.this.setResult(0, intent);
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("Type", 1);
                                break;
                            case 2:
                                LoginActivity.this.setResult(0, intent);
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("Type", 2);
                                break;
                        }

                        intent.putExtra("UserName", userName);
                        ;
                        intent.putExtra("UserId", userId);
                        startActivity(intent);
                        finish();

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserPwd.setError(getString(R.string.error_incorrect_password));
                                UserPwd.requestFocus();
                            }
                        });

                    }

                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putSerializable("ErrorMsg",
                            MSG_REQUEST_TIMEOUT);
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putSerializable("ErrorMsg",
                            MSG_RESPONSE_TIMEOUT);
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (ServiceRulesException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putSerializable("ErrorMsg", e.getMessage());
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putSerializable("ErrorMsg", MSG_LOGIN_ERROR);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }

            }
        });
        thread.start();
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    private int getIdentity() {
        int identity = 0;
        radioGroup = (RadioGroup) findViewById(R.id.login_radioGroup);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.login_identity_student:
                identity = 1;
                break;
            case R.id.login_identity_teacher:
                identity = 2;
                break;
        }
        return identity;

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public  void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            LoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            ProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            LoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }

    private  class IHandler extends Handler {

        private final WeakReference<Activity> mActivity;

        public IHandler(LoginActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {



            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable(
                            "ErrorMsg");
                    ((LoginActivity) mActivity.get()).showTip(errorMsg);
                    showProgress(false);
                    break;
                case FLAG_LOGIN_SUCCESS:
                    ((LoginActivity) mActivity.get()).showTip(MSG_LOGIN_SUCCESS);
                    break;
                default:
                    break;
            }
        }

    }

    private IHandler handler = new IHandler(this);

}


