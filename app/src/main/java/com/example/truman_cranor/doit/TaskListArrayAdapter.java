package com.example.truman_cranor.doit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.truman_cranor.doit.R;

import java.util.ArrayList;

/**
 * Created by truman_cranor on 9/23/16.
 */

public class TaskListArrayAdapter extends ArrayAdapter<Task> {

    public TaskListArrayAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        // Create a new view if we aren't recycling an old one
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_entry, parent, false);
        }

        TextView taskText = (TextView) convertView.findViewById(R.id.tvTaskText);
        taskText.setText(task.text);

        return convertView;
    }
}
