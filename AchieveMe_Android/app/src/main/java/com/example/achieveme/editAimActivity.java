package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.Aim;
import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Aims.SubAimsAdapter;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class editAimActivity extends AppCompatActivity {

    TextView name;
    TextView date;
    TextView time;
    TextView description;

    SharedPreferences creds;
    String username;
    String password;

    int aim_id;
    int list_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_aim);

        Intent intent = getIntent();
        aim_id = intent.getIntExtra(AimsActivity.AIMID, 1);
        list_id = intent.getIntExtra(ListsActivity.LISTID, 1);

        name = findViewById(R.id.nameView);
        date = findViewById(R.id.dateView);
        time = findViewById(R.id.timeView);
        description = findViewById(R.id.descriptionView);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);
        AimService aimService = ApiUtils.getAimService();
        Call<AimRes> call = aimService.aimInfo(username, list_id, aim_id, password);

        call.enqueue(new Callback<AimRes>() {
            @Override
            public void onResponse(Call<AimRes> call, Response<AimRes> response) {
                if (response.isSuccessful()) {
                    AimRes aim = response.body();
                    String deadline_s = aim.getFields().getDeadline();
                    try {
                        name.setText(aim.getFields().getName());
                        Date deadline_date = AimViewActivity.format.parse(deadline_s);
                        date.setText(AimViewActivity.format_date.format(deadline_date));
                        time.setText(AimViewActivity.format_time.format(deadline_date));
                    } catch (ParseException e) {
                        Toast.makeText(editAimActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //descrView.setText();
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(editAimActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AimRes> call, Throwable t) {
                Toast.makeText(editAimActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sendInfo(View view) {
        Aim new_aim = new Aim(
                name.getText().toString(),
                date.getText().toString(),
                time.getText().toString(),
                description.getText().toString());

        AimService aimService = ApiUtils.getAimService();
        Call<Aim> call = aimService.editAim(username, list_id, aim_id, password, new_aim);

        call.enqueue(new Callback<Aim>() {
            @Override
            public void onResponse(Call<Aim> call, Response<Aim> response) {
                if (!response.isSuccessful()) {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(editAimActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Aim> call, Throwable t) {
                Toast.makeText(editAimActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
