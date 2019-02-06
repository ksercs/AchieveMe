package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.animation.MotionSpec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class MainEmptyActivity extends AppCompatActivity {

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent activityIntent;
        SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        if (creds.contains(LoginActivity.USERNAME)
         && creds.contains(LoginActivity.PASSWORD)) {
            activityIntent = new Intent(MainEmptyActivity.this, MainActivity.class);
        } else {
            activityIntent = new Intent(MainEmptyActivity.this, LoginActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}
