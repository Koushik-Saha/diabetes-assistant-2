package com.jets.medicalreminder.splash;

import android.app.Activity;
import android.os.Bundle;

import com.jets.medicalreminder.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onBackPressed() {
    }

}
