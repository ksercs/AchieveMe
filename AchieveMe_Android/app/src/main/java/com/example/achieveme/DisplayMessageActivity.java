package com.example.achieveme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        // получаем экземпляр элемента ListView
        final ListView listView = findViewById(R.id.listView);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://10.23.74.12:8000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        AchieveMeClient client = retrofit.create(AchieveMeClient.class);
        Intent intent = getIntent();
        String login = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Call<List<Model>> call = client.userAims(login);

        call.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                List<Model> aims = response.body();
                listView.setAdapter(new AimAdapter(DisplayMessageActivity.this, aims));
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {
                Toast.makeText(DisplayMessageActivity.this, "Ошибка :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
