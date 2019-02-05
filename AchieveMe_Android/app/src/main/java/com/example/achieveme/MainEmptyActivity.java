package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class MainEmptyActivity extends AppCompatActivity {

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent activityIntent;
        SharedPreferences.Editor ed3 = getSharedPreferences("creds", MODE_PRIVATE).edit();
        ed3.clear();
        ed3.apply();
        if (getSharedPreferences("creds", MODE_PRIVATE).contains(LoginActivity.EXTRA_USERNAME)) {
            activityIntent = new Intent(MainEmptyActivity.this, MainActivity.class);
        } else {
            activityIntent = new Intent(MainEmptyActivity.this, LoginActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}
