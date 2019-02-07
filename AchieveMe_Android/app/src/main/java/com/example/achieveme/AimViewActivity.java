package com.example.achieveme;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Aims.SubAimsAdapter;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimViewActivity extends BaseActivity {

    @Override
    int getContentViewId() {
        return R.layout.activity_aim_view;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        final View header = getLayoutInflater().inflate(R.layout.aim_header, null);
        final ImageView avatarView = header.findViewById(R.id.avatarView);
        //TextView descrView = findViewById(R.id.descrView);
        final TextView deadlineDateView = header.findViewById(R.id.deadlineDateView);
        final TextView deadlineTimeView = header.findViewById(R.id.deadlineTimeView);
        final ListView subaimsList = findViewById(R.id.subaimsListView);

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

                    new AsyncTaskLoadImage(avatarView).execute(ApiUtils.BASE_URL + "media/" + aim.getFields().getImage());

                    subaimsList.addHeaderView(header);
                    List<SubAimRes> subaims = aim.getSubaims();
                    subaimsList.setAdapter(new SubAimsAdapter(AimViewActivity.this, subaims));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
