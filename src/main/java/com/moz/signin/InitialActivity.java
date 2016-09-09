package com.moz.signin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        startLoginActivity();
    }
    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent=new Intent(InitialActivity.this,LoginActivity.class);
                startActivity(intent);
                InitialActivity.this.finish();//结束本Activity
            }
        }, 1000);//设置执行时间
    }
}
