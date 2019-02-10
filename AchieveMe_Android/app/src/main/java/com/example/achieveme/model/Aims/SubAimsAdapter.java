package com.example.achieveme.model.Aims;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.graphics.Paint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achieveme.AimViewActivity;
import com.example.achieveme.LoginActivity;
import com.example.achieveme.R;
import com.example.achieveme.editAimActivity;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SubAimsAdapter extends ArrayAdapter {

    private Context context;
    private List<SubAimRes> values;

    public SubAimsAdapter(Context context, List<SubAimRes> values) {
        super(context, R.layout.subaim_list_item, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.subaim_list_item, parent, false);
        }
        final CheckBox completed = row.findViewById(R.id.isCompleted);
        final TextView textView = row.findViewById(R.id.aimNameView);
        TextView dateView = row.findViewById(R.id.dateView);
        ImageView important = row.findViewById(R.id.important);

        final SubAimRes item = values.get(position);

        completed.setOnCheckedChangeListener(null);
        completed.setChecked(item.getFields().isIs_completed());
        if (completed.isChecked()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    values.get(position).getFields().setIs_completed(true);
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    values.get(position).getFields().setIs_completed(false);
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                final SharedPreferences creds = context.getSharedPreferences("creds", MODE_PRIVATE);
                String username = creds.getString(LoginActivity.USERNAME, null);
                String password = creds.getString(LoginActivity.PASSWORD, null);
                AimService aimService = ApiUtils.getAimService();
                Call<SubAimRes> call = aimService.markAim(
                        username,
                        item.getFields().getList_id(),
                        item.getId(),
                        password);

                call.enqueue(new Callback<SubAimRes>() {
                    @Override
                    public void onResponse(Call<SubAimRes> call, Response<SubAimRes> response) {
                    }

                    @Override
                    public void onFailure(Call<SubAimRes> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        textView.setText(item.getFields().getName());
        textView.setTag(item.getId());
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final SimpleDateFormat format_date = new SimpleDateFormat("dd-MM-yy");
        String deadline_s = item.getFields().getDeadline();
        try {
            Date deadline_date = format.parse(deadline_s);
            dateView.setText(format_date.format(deadline_date));
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (item.getFields().isIs_imortant()) {
            important.setImageResource(R.drawable.btn_rating_star_on_focused_holo_light);
        } else {
            important.setImageDrawable(null);
        }
        row.setOnCreateContextMenuListener(null);
        return row;
    }
}
