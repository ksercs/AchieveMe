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

import com.example.achieveme.model.Aims.Aim;
import com.example.achieveme.model.Aims.AimFields;
import com.example.achieveme.model.Aims.AimsAdapter;
import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Analysis.AimAnalysis;
import com.example.achieveme.model.Analysis.SubAimAnalysis;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.AimsListService;
import com.example.achieveme.remote.AnalysisService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    Comparator<SubAimRes> comp;

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

        comp = new Comparator<SubAimRes>() {
            @Override
            public int compare(SubAimRes o1, SubAimRes o2) {
                if (o2.getFields().isIs_completed() && !o1.getFields().isIs_completed()) {
                    return -1;
                }
                if (o1.getFields().isIs_completed() && !o2.getFields().isIs_completed()) {
                    return 1;
                }
                return o1.getFields().getDeadline().compareTo(o2.getFields().getDeadline());
            }
        };

        AimsListService aimsListService = ApiUtils.getAimsService();
        Call<List<SubAimRes>> call = aimsListService.userAims(username, list_id, password);

        call.enqueue(new Callback<List<SubAimRes>>() {
            @Override
            public void onResponse(Call<List<SubAimRes>> call, Response<List<SubAimRes>> response) {
                if (response.isSuccessful()) {
                    aims = response.body();
                    Collections.sort(aims, comp);
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
                intent.putExtra("pos", position);
                startActivityForResult(intent, 3);
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
        int position = 0;
        for (int i = 0; i < aims.size(); ++i) {
            if (listView.getChildAt(i).findViewById(view.getId()).getTag() == view.getTag()) {
                position = i;
                break;
            }
        }
        intent.putExtra("pos", position);
        startActivityForResult(intent, 3);
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
        switch (requestCode) {
            case 1: {
                String name = data.getStringExtra("new_name");
                String date = data.getStringExtra("new_date");
                String time = data.getStringExtra("new_time");
                int pos = data.getIntExtra("pos", 1);
                int subaim_id = data.getIntExtra("aim_id", 1);
                String image = data.getStringExtra("image");

                if (pos < 0) {
                    aims.add(new SubAimRes(subaim_id, new AimFields(name, date + "T" + time)));
                    Collections.sort(aims, comp);
                    adapter.notifyDataSetChanged();
                    View item = listView.getChildAt(listView.getLastVisiblePosition());
                    ImageView avatar = item.findViewById(R.id.aimAvatarView);
                    new AsyncTaskLoadImage(avatar).execute(ApiUtils.BASE_URL + "media/" + image);
                    return;
                }
                aims.set(pos, new SubAimRes(subaim_id, new AimFields(name, date + "T" + time)));
                Collections.sort(aims, comp);
                adapter.notifyDataSetChanged();
                break;
            }
            case 2: {
                if (resultCode == RESULT_OK) {
                    ArrayList phrases = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = ((String) phrases.get(0));
                    AimAnalysis aim = new AimAnalysis(text);

                    AnalysisService analysisService = ApiUtils.getAnalysisService();
                    Call<SubAimRes> call = analysisService.voiceAim(username, list_id, aim, password);

                    call.enqueue(new Callback<SubAimRes>() {
                        @Override
                        public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                            if (response.isSuccessful()) {
                                SubAimRes new_aim = response.body();
                                aims.add(new_aim);
                                Collections.sort(aims, comp);
                                adapter.notifyDataSetChanged();
                                return;
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
                        public void onFailure(Call<SubAimRes> call, Throwable t) {
                            Toast.makeText(AimsActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
            case 3: {
                int pos = data.getIntExtra("pos", 1);
                int cur = data.getIntExtra("cur", 0);
                int all = data.getIntExtra("all", 1);
                aims.get(pos).getFields().setCur_points(cur);
                aims.get(pos).getFields().setAll_points(all);
                adapter.notifyDataSetChanged();
            }
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
        int aimId = (int) t.findViewById(R.id.aimNameView).getTag();
        switch (menuItemIndex) {
            case R.id.Edit: {
                Intent intent = new Intent(AimsActivity.this, editAimActivity.class);
                intent.putExtra(ListsActivity.LISTID, list_id);
                intent.putExtra(AimsActivity.AIMID, aimId);
                intent.putExtra("pos", info.position);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.Delete: {
                AimService aimService = ApiUtils.getAimService();
                Call<SubAimRes> call = aimService.deleteAim(username, list_id, aimId, password);
                call.enqueue(new Callback<SubAimRes>() {
                    @Override
                    public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
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
                    public void onFailure(Call<SubAimRes> call, Throwable t) {
                        Toast.makeText(AimsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
            case R.id.Important: {
                AimService aimService = ApiUtils.getAimService();
                Call<SubAimRes> call = aimService.impAim(username, aimId, password);
                call.enqueue(new Callback<SubAimRes>() {
                    @Override
                    public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                        if (response.isSuccessful()) {
                            aims.get(info.position).getFields().setIs_important(!aims.get(info.position).getFields().isIs_important());
                            adapter.notifyDataSetChanged();
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
                    public void onFailure(Call<SubAimRes> call, Throwable t) {
                        Toast.makeText(AimsActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
                break;
            }
        }
        return true;
    }

    public void speakAdd(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 2);
    }
}
