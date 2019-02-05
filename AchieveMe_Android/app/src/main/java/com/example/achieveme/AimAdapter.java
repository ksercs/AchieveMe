package com.example.achieveme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.achieveme.model.Model;

import java.util.List;

public class AimAdapter extends ArrayAdapter {

    private Context context;
    private List<Model> values;

    public AimAdapter(Context context, List<Model> values) {
        super(context, android.R.layout.simple_list_item_1, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = row.findViewById(android.R.id.text1);

        Model item = values.get(position);

        String message = item.getFields().getName();
        textView.setText(message);

        return row;
    }
}
