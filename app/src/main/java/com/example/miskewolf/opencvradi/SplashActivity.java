package com.example.miskewolf.opencvradi;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import steelkiwi.com.library.DotsLoaderView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=3000;
    DotsLoaderView dotsLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dotsLoader=(DotsLoaderView)findViewById(R.id.loader);
        Start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashscreen = new Intent(SplashActivity.this, Recognition.class);
                startActivity(splashscreen);
                finish();
            }
        },SPLASH_TIME_OUT);


    }
    private void Start(){
        dotsLoader.show();
        dotsLoader.setVisibility(View.VISIBLE);

    }
    private void Cancel() {
        dotsLoader.setVisibility(View.INVISIBLE);
    }
}
