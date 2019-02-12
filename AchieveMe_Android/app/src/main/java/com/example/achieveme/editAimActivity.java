package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class editAimActivity extends AppCompatActivity {

    EditText name;
    EditText date;
    EditText time;
    EditText description;

    SharedPreferences creds;
    String username;
    String password;

    int aim_id;
    int list_id;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_aim);

        Intent intent = getIntent();
        aim_id = intent.getIntExtra(AimsActivity.AIMID, -1);
        list_id = intent.getIntExtra(ListsActivity.LISTID, 1);


        pos = intent.getIntExtra("pos", -1);

        name = findViewById(R.id.nameView);
        date = (EditText) findViewById(R.id.dateView);
        time = findViewById(R.id.timeView);
        description = findViewById(R.id.descriptionView);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);

        TextWatcher dtw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ГГГГММДД";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    if (cl >= 4) {
                        ++sel;
                    }
                    if (cl >= 6) {
                        ++sel;
                    }

                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(6,8));
                        int mon  = Integer.parseInt(clean.substring(4,6));
                        int year = Integer.parseInt(clean.substring(0,4));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012
                        day = day < 1 ? 1 : day;
                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format(Locale.US, "%02d%02d%02d",year, mon, day);
                    }

                    clean = String.format("%s-%s-%s", clean.substring(0, 4),
                            clean.substring(4, 6),
                            clean.substring(6, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date.setText(current);
                    date.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher ttw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ЧЧММ";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    if (cl >= 2) {
                        ++sel;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 4){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int hours = Integer.parseInt(clean.substring(0,2));
                        int mins = Integer.parseInt(clean.substring(2,4));

                        hours = hours < 0 ? 0 : hours > 23 ? 23 : hours;
                        cal.set(Calendar.HOUR, hours);
                        mins = mins < 0 ? 0 : mins > 60 ? 60 : mins;
                        cal.set(Calendar.MINUTE, mins);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012
                        clean = String.format(Locale.US, "%02d%02d",hours, mins);
                    }

                    clean = String.format("%s:%s", clean.substring(0, 2),
                            clean.substring(2, 4));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    time.setText(current);
                    time.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        date.addTextChangedListener(dtw);
        time.addTextChangedListener(ttw);

        if (pos >= 0) {
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

                        description.setText(aim.getFields().getDescription().getFields().getText());
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
    }


    public void sendInfo(View view) {
        Date now = new Date();
        if (date.getText().toString().isEmpty() || date.getText().toString().equals("ГГГГ-ММ-ДД")) {
            date.setText((new SimpleDateFormat("yyyy-MM-dd")).format(now));
        }
        if (time.getText().toString().isEmpty() || time.getText().toString().equals("ЧЧ:ММ")) {
            time.setText((new SimpleDateFormat("HH:mm")).format(now));
        }
        if (name.getText().toString().isEmpty()) {
            name.setText("Новая цель");
        }
        Aim new_aim = new Aim(
                name.getText().toString(),
                date.getText().toString(),
                time.getText().toString(),
                description.getText().toString());
        new_aim.setParent_id(pos < 0 ? aim_id : -1);
        AimService aimService = ApiUtils.getAimService();
        Call<SubAimRes> call =  pos >= 0 ?
                aimService.editAim(username, list_id, aim_id, password, new_aim) :
                aimService.newAim(username, list_id, new_aim, password);

        call.enqueue(new Callback<SubAimRes>() {
            @Override
            public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                if (!response.isSuccessful()) {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(editAimActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                SubAimRes new_aim = response.body();
                aim_id = new_aim.getId();
                Intent intent = new Intent();
                intent.putExtra("image", new_aim.getFields().getImage());
                intent.putExtra("new_name", name.getText().toString());
                intent.putExtra("new_date", date.getText().toString());
                intent.putExtra("new_time", time.getText().toString());
                intent.putExtra("pos", pos);
                intent.putExtra("aim_id", aim_id);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<SubAimRes> call, Throwable t) {
                Toast.makeText(editAimActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
