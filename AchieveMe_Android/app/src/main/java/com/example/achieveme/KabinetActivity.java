package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class KabinetActivity extends BaseActivity {

    SharedPreferences creds;

    @Override
    int getContentViewId() {
        return R.layout.activity_kabinet;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_notifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        creds = getSharedPreferences(LoginActivity.USERNAME, MODE_PRIVATE);

    }

    public void logout(View v) {
        SharedPreferences.Editor edit = creds.edit();
        edit.clear();
        edit.apply();
        Intent intent = new Intent(KabinetActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
