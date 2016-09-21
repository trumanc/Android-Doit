package com.example.truman_cranor.doit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.truman_cranor.doit.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listItems;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    private final static String ITEM_FILE_NAME = "todo.txt";
    private final static String LOG_TAG = "DoItMain";

    private static final int EDIT_ITEM_ACTIVITY_CODE = 1;
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
        setupListViewClickListeners();
    }

    private void setupListViewClickListeners() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        listItems.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(getApplicationContext(), listItems.get(pos), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra(EditItemActivity.INTENT_EXTRA_TEXT, listItems.get(pos));
                i.putExtra(EditItemActivity.INTENT_EXTRA_INDEX, pos);
                startActivityForResult(i, EDIT_ITEM_ACTIVITY_CODE);

                Toast.makeText(getApplicationContext(), listItems.get(pos), Toast.LENGTH_SHORT).show();
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
        listItems.set(pos, text);
        itemsAdapter.notifyDataSetChanged();
        writeItems();
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
