package com.example.achieveme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.model.Aims.Aim;
import com.example.achieveme.model.Aims.AimFields;
import com.example.achieveme.model.Aims.AimsAdapter;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.AimsListService;
import com.example.achieveme.remote.ApiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AimsActivity extends BaseActivity {

    public static final String AIMID = "com.example.achieveme.AIMID";
    public static final String AIMNAME = "com.example.achieveme.AIMNAME";
    private int list_id;
    List<SubAimRes> aims;

    ListView listView;
    AimsAdapter adapter;

    SharedPreferences creds;
    String username;
    String password;

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

        listView = findViewById(R.id.aimsListView);
        registerForContextMenu(listView);

        creds = getSharedPreferences("creds", MODE_PRIVATE);
        username = creds.getString(LoginActivity.USERNAME, null);
        password = creds.getString(LoginActivity.PASSWORD, null);

        AimsListService aimsListService = ApiUtils.getAimsService();
        Call<List<SubAimRes>> call = aimsListService.userAims(username, list_id, password);

        call.enqueue(new Callback<List<SubAimRes>>() {
            @Override
            public void onResponse(Call<List<SubAimRes>> call, Response<List<SubAimRes>> response) {
                if (response.isSuccessful()) {
                    aims = response.body();
                    adapter = new AimsAdapter(AimsActivity.this, aims);
                    listView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_aims, menu);
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
        if (id == R.id.newAim) {
            Intent intent = new Intent(AimsActivity.this, editAimActivity.class);
            intent.putExtra(ListsActivity.LISTID, list_id);
            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String name = data.getStringExtra("new_name");
        String date = data.getStringExtra("new_date");
        int pos = data.getIntExtra("pos", 1);
        int subaim_id = data.getIntExtra("aim_id", 1);
        if (pos < 0) {
            aims.add(new SubAimRes(subaim_id, new AimFields(name, date + "T00:00:00Z")));
            adapter.notifyDataSetChanged();
            return;
        }
        View t = AimViewActivity.getViewByPosition(pos, listView);
        TextView nameView = t.findViewById(R.id.aimNameView);
        TextView dateView = t.findViewById(R.id.dateView);
        nameView.setText(name);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat format_date = new SimpleDateFormat("dd-MM-yy");
        try {
            dateView.setText(format_date.format(format.parse(date)));
        } catch (ParseException e) {
            Toast.makeText(AimsActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aim_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        View t = AimViewActivity.getViewByPosition(info.position, listView);
        int subaimId = (int) t.findViewById(R.id.aimNameView).getTag();
        switch (menuItemIndex) {
            case R.id.Edit: {
                Intent intent = new Intent(AimsActivity.this, editAimActivity.class);
                intent.putExtra(ListsActivity.LISTID, list_id);
                intent.putExtra(AimsActivity.AIMID, subaimId);
                intent.putExtra("pos", info.position);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.Delete: {
                AimService aimService = ApiUtils.getAimService();
                Call<Aim> call = aimService.deleteAim(username, list_id, subaimId, password);
                call.enqueue(new Callback<Aim>() {
                    @Override
                    public void onResponse(Call<Aim> call, Response<Aim> response) {
                        if (!response.isSuccessful()) {
                            SharedPreferences.Editor edit = creds.edit();
                            edit.clear();
                            edit.apply();
                            Intent intent = new Intent(AimsActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        aims.remove(aims.get(info.position));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Aim> call, Throwable t) {
                        Toast.makeText(AimsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
        }
        return true;
    }
}
