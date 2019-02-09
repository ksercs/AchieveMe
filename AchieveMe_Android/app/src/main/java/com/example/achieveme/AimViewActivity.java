package com.example.achieveme;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.AimFields;
import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Aims.SubAimsAdapter;
import com.example.achieveme.model.Analysis.AimAnalysis;
import com.example.achieveme.model.Analysis.SubAimAnalysis;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.AnalysisService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimViewActivity extends BaseActivity {

    View header;
    ImageView avatarView;
    TextView descrView;
    TextView deadlineDateView;
    TextView deadlineTimeView;
    ListView subaimsList;

    public final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public final static SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");

    public int list_id;
    int aim_id;

    SharedPreferences creds;
    String username;
    String password;

    List<SubAimRes> subaims;
    SubAimsAdapter adapter;

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
        list_id = intent.getIntExtra(ListsActivity.LISTID, 1);
        aim_id = intent.getIntExtra(AimsActivity.AIMID, 1);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);

        header = getLayoutInflater().inflate(R.layout.aim_header, null);
        avatarView = header.findViewById(R.id.avatarView);
        descrView = header.findViewById(R.id.descrView);
        deadlineDateView = header.findViewById(R.id.deadlineDateView);
        deadlineTimeView = header.findViewById(R.id.deadlineTimeView);
        subaimsList = findViewById(R.id.subaimsListView);
        registerForContextMenu(subaimsList);

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
                    String description = aim.getFields().getDescription().getFields().getText();
                    descrView.setText(description);

                    new AsyncTaskLoadImage(avatarView).execute(ApiUtils.BASE_URL + "media/" + aim.getFields().getImage());

                    subaimsList.addHeaderView(header);
                    subaims = aim.getSubaims();
                    adapter = new SubAimsAdapter(AimViewActivity.this, subaims);
                    subaimsList.setAdapter(adapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subaims, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.newSubAim: {
                Intent intent = new Intent(AimViewActivity.this, editAimActivity.class);
                intent.putExtra(ListsActivity.LISTID, list_id);
                intent.putExtra(AimsActivity.AIMID, aim_id);
                startActivityForResult(intent, 1);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (((AdapterView.AdapterContextMenuInfo) menuInfo).position < 1) {
            return;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aim_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        View t = getViewByPosition(info.position, subaimsList);
        int subaimId = (int) t.findViewById(R.id.aimNameView).getTag();
        switch (menuItemIndex) {
            case R.id.Edit: {
                Intent intent = new Intent(AimViewActivity.this, editAimActivity.class);
                intent.putExtra(ListsActivity.LISTID, list_id);
                intent.putExtra(AimsActivity.AIMID, subaimId);
                intent.putExtra("pos", info.position);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.Delete: {
                AimService aimService = ApiUtils.getAimService();
                Call<SubAimRes> call = aimService.deleteAim(username, list_id, subaimId, password);
                call.enqueue(new Callback<SubAimRes>() {
                    @Override
                    public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                        if (!response.isSuccessful()) {
                            SharedPreferences.Editor edit = creds.edit();
                            edit.clear();
                            edit.apply();
                            Intent intent = new Intent(AimViewActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        subaims.remove(subaims.get(info.position - 1));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<SubAimRes> call, Throwable t) {
                        Toast.makeText(AimViewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1: {
                String name = data.getStringExtra("new_name");
                String date = data.getStringExtra("new_date");
                int pos = data.getIntExtra("pos", 1);
                int subaim_id = data.getIntExtra("aim_id", 1);
                String image = data.getStringExtra("image");
                if (pos < 0) {
                    subaims.add(new SubAimRes(subaim_id, new AimFields(name, date + "T00:00:00Z")));
                    adapter.notifyDataSetChanged();
                    return;
                }
                View t = getViewByPosition(pos, subaimsList);
                TextView nameView = t.findViewById(R.id.aimNameView);
                TextView dateView = t.findViewById(R.id.dateView);
                nameView.setText(name);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                final SimpleDateFormat format_date = new SimpleDateFormat("dd-MM-yy");
                try {
                    dateView.setText(format_date.format(format.parse(date)));
                } catch (ParseException e) {
                    Toast.makeText(AimViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                }
                break;
            }
            case 2: {
                if (resultCode == RESULT_OK) {
                    ArrayList phrases = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = ((String) phrases.get(0));
                    SubAimAnalysis subaim = new SubAimAnalysis(text, aim_id);

                    AnalysisService analysisService = ApiUtils.getAnalysisService();
                    Call<SubAimRes> call = analysisService.voiceSubAim(username, list_id, subaim, password);

                    call.enqueue(new Callback<SubAimRes>() {
                        @Override
                        public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                            if (response.isSuccessful()) {
                                SubAimRes new_subaim = response.body();
                            }
                        }

                        @Override
                        public void onFailure(Call<SubAimRes> call, Throwable t) {

                        }
                    });
                }
            }
        }
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void speak(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 2);
    }
}
