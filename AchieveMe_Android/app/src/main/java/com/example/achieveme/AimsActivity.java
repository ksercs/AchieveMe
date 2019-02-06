package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Lists.AimsAdapter;
import com.example.achieveme.remote.AimsService;
import com.example.achieveme.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aims);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(ListsActivity.LISTNAME));
        int list_id = intent.getIntExtra(ListsActivity.LISTID, 1);

        final ListView listView = findViewById(R.id.aimsListView);

        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        AimsService aimsService = ApiUtils.getAimsService();
        Call<List<AimRes>> call = aimsService.userAims(username, list_id, password);

        call.enqueue(new Callback<List<AimRes>>() {
            @Override
            public void onResponse(Call<List<AimRes>> call, Response<List<AimRes>> response) {
                if (response.isSuccessful()) {
                    List<AimRes> aims = response.body();
                    listView.setAdapter(new AimsAdapter(AimsActivity.this, aims));
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(AimsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<AimRes>> call, Throwable t) {

            }
        });
    }
}
