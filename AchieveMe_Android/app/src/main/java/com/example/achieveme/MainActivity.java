package com.example.achieveme;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.achieveme.model.Model;
import com.example.achieveme.remote.AimsListService;
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

        AimsListService aimsListService = ApiUtils.getAimsListService();
        SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        Call<List<Model>> call = aimsListService.userAims(username, password);

        call.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                if (response.isSuccessful()) {
                    List<Model> aims = response.body();
                    listView.setAdapter(new AimAdapter(MainActivity.this, aims));
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
