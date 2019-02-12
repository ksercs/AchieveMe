package com.example.achieveme.model.Aims;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.achieveme.AimsActivity;
import com.example.achieveme.LoginActivity;
import com.example.achieveme.R;
import com.example.achieveme.remote.AimService;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import org.w3c.dom.Text;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AimsAdapter extends ArrayAdapter {

    private Context context;
    private List<SubAimRes> values;

    public AimsAdapter(Context context, List<SubAimRes> values) {
        super(context, R.layout.aim_list_item, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.aim_list_item, parent, false);
        }
        final CheckBox completed = row.findViewById(R.id.isCompleted);
        final TextView textView = row.findViewById(R.id.aimNameView);
        TextView dateView = row.findViewById(R.id.dateView);
        TextView timeView = row.findViewById(R.id.timeView);
        ImageView avatarView = row.findViewById(R.id.aimAvatarView);
        ImageView important = row.findViewById(R.id.important2);

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
        final SimpleDateFormat format_date_comp = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
        String deadline_s = item.getFields().getDeadline();
        try {
            final long day = 60 * 60 * 24 * 1000L;
            Date deadline_date = format.parse(deadline_s);
            String date = format_date.format(deadline_date);
            Date today = new Date();
            Date tomorrow = new Date(today.getTime() + day);
            if (format_date_comp.format(deadline_date).equals(format_date_comp.format(today))) {
                dateView.setText("Сегодня");
            } else if (format_date_comp.format(deadline_date).equals(format_date_comp.format(tomorrow))) {
                dateView.setText("Завтра ");
            } else {
                dateView.setText(date);
            }
            timeView.setText(format_time.format(deadline_date));
            if (deadline_date.getTime() - today.getTime() < day * 7) {
                dateView.setTextColor(dateView.getContext().getResources().getColor(R.color.yellow));
            }
            if (deadline_date.getTime() - today.getTime() < day) {
                dateView.setTextColor(dateView.getContext().getResources().getColor(R.color.orange));
            }
            if (deadline_date.getTime() - today.getTime() < 0) {
                dateView.setTextColor(dateView.getContext().getResources().getColor(R.color.red));
            }
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        new AsyncTaskLoadImage(avatarView).execute(ApiUtils.BASE_URL + "media/" + item.getFields().getImage());

        if (item.getFields().isIs_important()) {
            important.setImageResource(R.drawable.btn_rating_star_on_focused_holo_light);
        } else {
            important.setImageDrawable(null);
        }

        row.setOnCreateContextMenuListener(null);
        return row;
    }
}
