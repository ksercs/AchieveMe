package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Lists.ListModel;
import com.example.achieveme.model.Lists.ListRes;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.ListsService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewListActivity extends AppCompatActivity {

    SharedPreferences creds;
    String username;
    String password;

    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);

        name = findViewById(R.id.newListName);
    }

    public void sendList(View view) {
        final String list_name = name.getText().toString();
        ListModel new_list = new ListModel(list_name);
        ListsService listsService = ApiUtils.getListsService();
        Call<ListRes> call = listsService.createList(username, new_list, password);

        call.enqueue(new Callback<ListRes>() {
            @Override
            public void onResponse(Call<ListRes> call, Response<ListRes> response) {
                if (!response.isSuccessful()) {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(NewListActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                ListRes new_list = response.body();
                int list_id = new_list.getId();
                Intent intent = new Intent();
                intent.putExtra("list_id", list_id);
                intent.putExtra("list_name", list_name);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<ListRes> call, Throwable t) {
                Toast.makeText(NewListActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }
}
