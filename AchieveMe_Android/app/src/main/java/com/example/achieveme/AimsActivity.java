package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Aims.AimsAdapter;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.remote.AimsListService;
import com.example.achieveme.remote.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimsActivity extends BaseActivity {

    public static final String AIMID = "com.example.achieveme.AIMID";
    public static final String AIMNAME = "com.example.achieveme.AIMNAME";
    private int list_id;

    @Override
    int getContentViewId() {
        return R.layout.activity_aims;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(ListsActivity.LISTNAME));
        list_id = intent.getIntExtra(ListsActivity.LISTID, 0);

        final ListView listView = findViewById(R.id.aimsListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        AimsListService aimsListService = ApiUtils.getAimsService();
        Call<List<SubAimRes>> call = aimsListService.userAims(username, list_id, password);

        call.enqueue(new Callback<List<SubAimRes>>() {
            @Override
            public void onResponse(Call<List<SubAimRes>> call, Response<List<SubAimRes>> response) {
                if (response.isSuccessful()) {
                    List<SubAimRes> aims = response.body();
                    listView.setAdapter(new AimsAdapter(AimsActivity.this, aims));
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(AimsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<SubAimRes>> call, Throwable t) {
                Toast.makeText(AimsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(AimsActivity.this, AimViewActivity.class);
                intent.putExtra(ListsActivity.LISTID, list_id);
                intent.putExtra(AIMID, (Integer) itemClicked.getTag());
                intent.putExtra(AIMNAME, ((TextView) itemClicked).getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openAim(View view) {
        if (list_id == 0) {
            return;
        }
        Intent intent = new Intent(AimsActivity.this, AimViewActivity.class);
        intent.putExtra(ListsActivity.LISTID, list_id);
        intent.putExtra(AIMID, (Integer) view.getTag());
        intent.putExtra(AIMNAME, ((TextView) view).getText());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
