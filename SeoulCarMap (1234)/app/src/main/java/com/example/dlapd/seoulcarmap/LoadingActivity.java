package com.example.dlapd.seoulcarmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LoadingActivity extends Activity {
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mAuth = FirebaseAuth.getInstance();
        startLoading();

    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if(mAuth.getUid()!=null){
                    startActivity(new Intent(LoadingActivity.this,MainActivity.class));
                    finish();
                }
                else{
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                }
            }
        }, 2000);

    }
}