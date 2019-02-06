package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aim_view);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(AimsActivity.AIMNAME));
        int list_id = intent.getIntExtra(ListsActivity.LISTID, 1);
        int aim_id = intent.getIntExtra(AimsActivity.AIMID, 1);
        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");

        //TextView descrView = findViewById(R.id.descrView);
        final TextView deadlineDateView = findViewById(R.id.deadlineDateView);
        final TextView deadlineTimeView = findViewById(R.id.deadlineTimeView);

        AimService aimService = ApiUtils.getAimService();
        Call<AimRes> call = aimService.aimInfo(username, list_id, aim_id, password);

        call.enqueue(new Callback<AimRes>() {
            @Override
            public void onResponse(Call<AimRes> call, Response<AimRes> response) {
                if (response.isSuccessful()) {
                    AimRes aim = response.body();
                    String deadline_s = aim.getFields().getDeadline();
                    try {
                        Date deadline_date = format.parse(deadline_s);
                        deadlineDateView.setText(format_date.format(deadline_date));
                        deadlineTimeView.setText(format_time.format(deadline_date));
                    } catch (ParseException e) {
                        Toast.makeText(AimViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(AimViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AimRes> call, Throwable t) {
                Toast.makeText(AimViewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
