package com.example.truman_cranor.doit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;


public class MainActivity extends AppCompatActivity {
    TaskListArrayAdapter itemsAdapter;
    ExpandableListView elvItems;

    private final static String LOG_TAG = "DoItMain";

    private static final int EDIT_ITEM_ACTIVITY_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity boilerplate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView and Adapter instance vars
        elvItems = (ExpandableListView) findViewById(R.id.elvLists);
        itemsAdapter = new TaskListArrayAdapter(Task.readItems());
        elvItems.setAdapter(itemsAdapter);

        // Expand the uncompleted items at app open
        elvItems.expandGroup(TaskListArrayAdapter.GROUP_NUM_TODO);
        setupListViewClickListeners();
    }

    private void setupListViewClickListeners() {
        elvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        Log.d(LOG_TAG, "Inside long click handler");
                        if (ExpandableListView.getPackedPositionType(id) ==
                                ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                            int childPosition = ExpandableListView.getPackedPositionChild(id);

                            // Deletes from mysql, removes from backing array, and notifies the view
                            itemsAdapter.deleteTask(groupPosition, childPosition);
                            return true;
                        } else if (ExpandableListView.getPackedPositionType(id) ==
                                ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                            Log.d(LOG_TAG, "Ignoring longclick on list group");
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );

        elvItems.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                Task item = (Task) itemsAdapter.getChild(groupPosition, childPosition);
                i.putExtra(EditItemActivity.INTENT_EXTRA_TEXT, item.text);
                i.putExtra(EditItemActivity.INTENT_EXTRA_COMPLETED, item.completed);
                i.putExtra(EditItemActivity.INTENT_EXTRA_INDEX, childPosition);
                startActivityForResult(i, EDIT_ITEM_ACTIVITY_CODE);

                //Toast.makeText(getApplicationContext(), listItems.get(pos).text, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ITEM_ACTIVITY_CODE) {
            if (resultCode == EditItemActivity.RESULT_CODE_SUBMITTED) {

                updateItem(data.getExtras().getInt(EditItemActivity.INTENT_EXTRA_INDEX),
                        data.getExtras().getBoolean(EditItemActivity.INTENT_EXTRA_COMPLETED),
                        data.getExtras().getBoolean(EditItemActivity.INTENT_EXTRA_NEW_COMPLETED),
                        data.getExtras().getString(EditItemActivity.INTENT_EXTRA_TEXT));
            } else if (resultCode == RESULT_CANCELED) {
                // Do nothing, since the edit action was cancelled
            } else if (resultCode == EditItemActivity.RESULT_CODE_ERROR) {
                Log.d(LOG_TAG, "Error from the EditItemActivity. Can't update item");
            } else {
                throw new RuntimeException("Unknown resultCode from EditItemActivity");
            }
        } else {
            throw new RuntimeException("Unknown requestCode in MainActivity");
        }
    }

    private void updateItem(int pos, boolean oldCompleted, boolean newCompleted, String text) {
        int oldGroupNum =
                oldCompleted ?
                TaskListArrayAdapter.GROUP_NUM_COMPLETED :
                TaskListArrayAdapter.GROUP_NUM_TODO;
        Task edited = (Task) itemsAdapter.getChild(oldGroupNum, pos);
        edited.updateTask(text, newCompleted);

        if (newCompleted != oldCompleted) {
            itemsAdapter.changeCompletion(pos, newCompleted);
        }
        //listItems.get(pos).text = text;
        //listItems.get(pos).save();
        //itemsAdapter.notifyDataSetChanged();
    }

    public void onAddItem(View btnView) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);

        // Add item the adapter. This automatically adds it to the internal array, and updates
        itemsAdapter.addTask(etNewItem.getText().toString());
        etNewItem.setText("");
    }

}


