package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.achieveme.model.Lists.ListRes;
import com.example.achieveme.remote.ListsService;
import com.example.achieveme.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получаем экземпляр элемента ListView
        final ListView listView = findViewById(R.id.listView);

        ListsService listsService = ApiUtils.getAimsListService();
        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        Call<List<ListRes>> call = listsService.userAims(username, password);

        call.enqueue(new Callback<List<ListRes>>() {
            @Override
            public void onResponse(Call<List<ListRes>> call, Response<List<ListRes>> response) {
                if (response.isSuccessful()) {
                    List<ListRes> lists = response.body();
                    listView.setAdapter(new AimAdapter(MainActivity.this, lists));
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<ListRes>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
