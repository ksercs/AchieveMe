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

import com.example.achieveme.model.Lists.ListRes;
import com.example.achieveme.model.Lists.ListsAdapter;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.ListsService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListsActivity extends BaseActivity {

    @Override
    int getContentViewId() {
        return R.layout.activity_lists;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    public static final String LISTID = "com.example.achieveme.LISTID";
    public static final String LISTNAME = "com.example.achieveme.LISTNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListView listView = findViewById(R.id.listView);

        final SharedPreferences creds = getSharedPreferences("creds", MODE_PRIVATE);
        String username = creds.getString(LoginActivity.USERNAME, null);
        String password = creds.getString(LoginActivity.PASSWORD, null);

        ListsService listsService = ApiUtils.getListsService();
        Call<List<ListRes>> call = listsService.userAims(username, password);

        call.enqueue(new Callback<List<ListRes>>() {
            @Override
            public void onResponse(Call<List<ListRes>> call, Response<List<ListRes>> response) {
                if (response.isSuccessful()) {
                    List<ListRes> lists = response.body();
                    listView.setAdapter(new ListsAdapter(ListsActivity.this, lists));
                } else {
                    SharedPreferences.Editor edit = creds.edit();
                    edit.clear();
                    edit.apply();
                    Intent intent = new Intent(ListsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<ListRes>> call, Throwable t) {
                Toast.makeText(ListsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(ListsActivity.this, AimsActivity.class);
                intent.putExtra(LISTID, (Integer) itemClicked.getTag());
                intent.putExtra(LISTNAME, ((TextView) itemClicked).getText());
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
