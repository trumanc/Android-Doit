package com.example.truman_cranor.doit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.truman_cranor.doit.BuildConfig.DEBUG;
import static com.example.truman_cranor.doit.R.id.etNewItem;

public class MainActivity extends AppCompatActivity {
    ArrayList<Task> listItems;
    TaskListArrayAdapter itemsAdapter;
    ListView lvItems;

    private final static String LOG_TAG = "DoItMain";

    private static final int EDIT_ITEM_ACTIVITY_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity boilerplate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView and Adapter instance vars
        lvItems = (ListView) findViewById(R.id.lvItems);
        listItems = readItems();
        itemsAdapter = new TaskListArrayAdapter(this, listItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewClickListeners();
    }

    private void setupListViewClickListeners() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        deleteTask(listItems.get(pos));
                        listItems.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(getApplicationContext(), listItems.get(pos).text, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra(EditItemActivity.INTENT_EXTRA_TEXT, listItems.get(pos).text);
                i.putExtra(EditItemActivity.INTENT_EXTRA_INDEX, pos);
                startActivityForResult(i, EDIT_ITEM_ACTIVITY_CODE);

                Toast.makeText(getApplicationContext(), listItems.get(pos).text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ITEM_ACTIVITY_CODE) {
            if (resultCode == EditItemActivity.RESULT_CODE_SUBMITTED) {
                updateItem(data.getExtras().getInt(EditItemActivity.INTENT_EXTRA_INDEX),
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

    private void updateItem(int pos, String text) {
        listItems.get(pos).text = text;
        listItems.get(pos).save();
        itemsAdapter.notifyDataSetChanged();
    }

    public void onAddItem(View btnView) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);

        Task newTask = newTask(etNewItem.getText().toString());

        // Add item the adapter. This automatically adds it to the internal array, and updates
        itemsAdapter.add(newTask);
        etNewItem.setText("");
    }

    private ArrayList<Task> readItems() {
        List<Task> tasks = new Select()
                .from(Task.class)
                .execute();
        return new ArrayList<Task>(tasks);
    }

    private Task newTask(String text) {
        Task newTask = new Task(text);
        newTask.save();

        return newTask;
    }

    private void deleteTask(Task toDelete) {
        toDelete.delete();
    }

}


