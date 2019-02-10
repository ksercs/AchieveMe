package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Aims.SubAimsAdapter;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends BaseActivity {

    SharedPreferences creds;
    String username;
    String password;

    ListView planView;
    List<SubAimRes> aims;
    SubAimsAdapter adapter;
    Comparator<SubAimRes> comp;

    @Override
    int getContentViewId() {
        return R.layout.activity_plan;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_dashboard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        planView = findViewById(R.id.planView);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);


        comp = new Comparator<SubAimRes>() {
            @Override
            public int compare(SubAimRes o1, SubAimRes o2) {
                return o1.getFields().getDeadline().compareTo(o2.getFields().getDeadline());
            }
        };

        AimService aimService = ApiUtils.getAimService();
        Call<List<SubAimRes>> call = aimService.userAims(username, password);
        call.enqueue(new Callback<List<SubAimRes>>() {
            @Override
            public void onResponse(Call<List<SubAimRes>> call, Response<List<SubAimRes>> response) {
                if (response.isSuccessful()) {
                    aims = response.body();
                    Collections.sort(aims, comp);
                    adapter = new SubAimsAdapter(PlanActivity.this, aims);
                    planView.setAdapter(adapter);
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(PlanActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<SubAimRes>> call, Throwable t) {
                Toast.makeText(PlanActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }
}
