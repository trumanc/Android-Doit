package com.example.truman_cranor.doit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.truman_cranor.doit.R;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;
import static com.activeandroid.Cache.getContext;

/**
 * Created by truman_cranor on 9/23/16.
 */

public class TaskListArrayAdapter extends BaseExpandableListAdapter {

    // A list of 2 lists: one for todo items, and one for completed items.
    private ArrayList<ArrayList<Task>> itemLists;

    public static int GROUP_NUM_TODO = 0;
    public static int GROUP_NUM_COMPLETED = 1;
    private static String[] GROUP_NAMES = {"To Do:", "Completed: "};

    public TaskListArrayAdapter(List<Task> allTasks) {
        itemLists = new ArrayList<ArrayList<Task>>();
        itemLists.add(new ArrayList<Task>());
        itemLists.add(new ArrayList<Task>());

        for (Task t : allTasks) {
            if (t.completed) {
                itemLists.get(GROUP_NUM_COMPLETED).add(t);
            } else {
                itemLists.get(GROUP_NUM_TODO).add(t);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return GROUP_NAMES.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemLists.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return itemLists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemLists.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_group,
                    parent, false);
        }

        TextView groupTitle = (TextView) convertView.findViewById(R.id.tvGroupTitle);
        groupTitle.setText(GROUP_NAMES[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_entry,
                    parent, false);
        }

        Task child = (Task) getChild(groupPosition, childPosition);
        TextView itemText = (TextView) convertView.findViewById(R.id.tvTaskText);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbItemCompleted);

        cb.setChecked(groupPosition == GROUP_NUM_COMPLETED);
        final TaskListArrayAdapter arrayAdapter = this;

        /* Need to reset the checkbox's onClickListener everytime this is called because the views
         * may be recycled between elements.
         */
        cb.setOnClickListener(new View.OnClickListener() {
            /* Use the OnClickListener class scope to save information (the adapter and indices)
             * that the onClick callback will need to identify which task was edited and then
             * communicate that change to the parent views.
             */
            int groupNum = groupPosition;
            int childIndex = childPosition;
            TaskListArrayAdapter parentAdapter = arrayAdapter;
            @Override
            public void onClick(View v) {
                arrayAdapter.changeCompletion(childPosition, ((CheckBox)v).isChecked());
            }
        });
        itemText.setText(child.text);

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void deleteTask(int groupPosition, int childPosition) {
        Task toDelete = itemLists.get(groupPosition).get(childPosition);
        Task.deleteTask(toDelete);

        itemLists.get(groupPosition).remove(childPosition);
        notifyDataSetChanged();
    }

    public void addTask(String text) {
        Task newTask = Task.newTask(text);
        itemLists.get(GROUP_NUM_TODO).add(newTask);

        notifyDataSetChanged();
    }


    /* If newCompleteState is true then we are moving an item from the 'TODO' state into the
     * 'COMPLETED' state.
     */
    public void changeCompletion(int childPosition, boolean newCompleteState) {
        int oldGroup = newCompleteState ? GROUP_NUM_TODO : GROUP_NUM_COMPLETED;
        int newGroup = newCompleteState ? GROUP_NUM_COMPLETED : GROUP_NUM_TODO;

        Task toMove = itemLists.get(oldGroup).get(childPosition);
        toMove.completed = newCompleteState;
        toMove.save();
        itemLists.get(oldGroup).remove(childPosition);

        itemLists.get(newGroup).add(toMove);

        notifyDataSetChanged();
    }


}
