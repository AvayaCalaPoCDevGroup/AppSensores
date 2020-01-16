package com.example.appsensores.ui.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.appsensores.Clases.STimer;
import com.example.appsensores.Clases.Utils;
import com.example.appsensores.R;

public class SplashActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    private SharedPreferences sharedPreferencesAvaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesAvaya = getSharedPreferences(Utils.AVAYA_SHARED_PREFERENCES,0);
        STimer.CURRENT_PERIOD = sharedPreferencesAvaya.getInt(Utils.AVAYA_INTERVALO,3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }
        }, 2000);
    }
}
