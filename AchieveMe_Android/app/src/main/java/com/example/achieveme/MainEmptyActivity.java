package com.example.achieveme;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class MainEmptyActivity extends AppCompatActivity {

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;
        if (getPreferences(MODE_PRIVATE).getString("username", null) != null) {
            activityIntent = new Intent(MainEmptyActivity.this, MainActivity.class);
        } else {
            activityIntent = new Intent(MainEmptyActivity.this, LoginActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}
