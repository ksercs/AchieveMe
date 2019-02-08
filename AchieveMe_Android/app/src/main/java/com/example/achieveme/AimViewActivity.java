package com.example.achieveme;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
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

    View header;
    ImageView avatarView;
    TextView descrView;
    TextView deadlineDateView;
    TextView deadlineTimeView;
    ListView subaimsList;

    public final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public final static SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");

    int list_id;

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
        int aim_id = intent.getIntExtra(AimsActivity.AIMID, 1);
        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        header = getLayoutInflater().inflate(R.layout.aim_header, null);
        avatarView = header.findViewById(R.id.avatarView);
        descrView = findViewById(R.id.descrView);
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

                    //descrView.setText();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subaims, menu);
        return true;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aim_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        View t = getViewByPosition(info.position, subaimsList);
        int aimId = (int) t.findViewById(R.id.aimNameView).getTag();
        if (menuItemIndex == R.id.Edit) {
            Intent intent = new Intent(AimViewActivity.this, editAimActivity.class);
            intent.putExtra(ListsActivity.LISTID, list_id);
            intent.putExtra(AimsActivity.AIMID, aimId);
            startActivity(intent);
        }
        return true;
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
}
