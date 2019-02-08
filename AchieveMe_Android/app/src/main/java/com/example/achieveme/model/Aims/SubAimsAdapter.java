package com.example.achieveme.model.Aims;

import android.content.Context;
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

import com.example.achieveme.R;
import com.example.achieveme.remote.ApiUtils;
import com.example.achieveme.remote.AsyncTaskLoadImage;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

        SubAimRes item = values.get(position);

        completed.setChecked(item.getFields().isIs_completed());
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
            }
        });

        textView.setText(item.getFields().getName());
        textView.setTag(item.getId());

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final SimpleDateFormat format_date = new SimpleDateFormat("dd-MM-yy");
        String deadline_s = item.getFields().getDeadline();
        try {
            Date deadline_date = format.parse(deadline_s);
            dateView.setText(format_date.format(deadline_date));
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        row.setOnCreateContextMenuListener(null);
        return row;
    }
}