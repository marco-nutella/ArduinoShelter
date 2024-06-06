package com.example.raduinoandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RadioAdapter extends ArrayAdapter<Item> {

    private int resourceLayout;

    private static class ViewHolder {
        TextView nameTextView;
        TextView staticTextView;
        TextView variableTextView;
    }

    public RadioAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceLayout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.name_text_view);
            viewHolder.staticTextView = convertView.findViewById(R.id.static_text_view);
            viewHolder.variableTextView = convertView.findViewById(R.id.variable_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(item.getName());
        viewHolder.staticTextView.setText("Channel:"); // Static text
        viewHolder.variableTextView.setText(String.valueOf(item.getVariable()));

        return convertView;
    }
}
