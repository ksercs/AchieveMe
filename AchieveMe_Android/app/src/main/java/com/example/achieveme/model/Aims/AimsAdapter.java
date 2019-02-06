package com.example.achieveme.model.Aims;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.achieveme.R;

import java.util.List;

public class AimsAdapter extends ArrayAdapter {

    private Context context;
    private List<AimRes> values;

    public AimsAdapter(Context context, List<AimRes> values) {
        super(context, R.layout.aim_list_item, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.aim_list_item, parent, false);
        }
        CheckBox completed = row.findViewById(R.id.isCompleted);
        final TextView textView = row.findViewById(R.id.aimNameView);

        AimRes item = values.get(position);

        completed.setChecked(item.getFields().isIs_made());
        textView.setText(item.getFields().getName());
        textView.setTag(item.getId());

        return row;
    }
}
