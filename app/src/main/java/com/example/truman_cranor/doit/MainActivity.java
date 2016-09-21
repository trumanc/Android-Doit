package com.example.truman_cranor.doit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.truman_cranor.doit.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listItems;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    private final static String ITEM_FILE_NAME = "todo.txt";
    private final static String LOG_TAG = "DoItMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity boilerplate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inititalize ListView and Adapter instance vars
        lvItems = (ListView) findViewById(R.id.lvItems);
        listItems = readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewLongClickListener();
    }

    private void setupListViewLongClickListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        listItems.remove(pos);
                        writeItems();
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );
    }

    public void onAddItem(View btnView) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String newItemText = etNewItem.getText().toString();
        itemsAdapter.add(newItemText);
        etNewItem.setText("");

        // TODO: Is there a more efficient way to add new items to persistent data?
        writeItems();
    }

    private ArrayList<String> readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, ITEM_FILE_NAME);
        try {
            return new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            Log.d(LOG_TAG, "Unable to read todo list file.", e);
            return new ArrayList<>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, ITEM_FILE_NAME);
        try {
            FileUtils.writeLines(todoFile, listItems);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Unable to write todo list file.", e);
            if (DEBUG) {
                // Force a quick crash so the dev can't miss this :)
                throw new RuntimeException(e);
            }
        }
    }
}
