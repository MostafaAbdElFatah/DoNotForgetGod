package com.example.user.donotforgetgod.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.donotforgetgod.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent page = new Intent();
                page.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(page);
                finish();
            }
        }, 5000);
    }
}
