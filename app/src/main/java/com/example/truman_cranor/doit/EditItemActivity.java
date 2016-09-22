package com.example.truman_cranor.doit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.R.attr.data;

public class EditItemActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_TEXT = "item_text";
    public static final String INTENT_EXTRA_INDEX = "item_pos";

    public static final int RESULT_CODE_SUBMITTED = 1;
    public static final int RESULT_CODE_CANCELLED = 2;
    public static final int RESULT_CODE_ERROR = -1;

    private static final String LOG_TAG = "EditItemActivity";
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        pos = getIntent().getIntExtra(INTENT_EXTRA_INDEX, -1);
        if (pos == -1) {
            throw new RuntimeException("Illegal pos value read in EditItemActivity:onCreate");
        }

        String oldText = getIntent().getStringExtra(INTENT_EXTRA_TEXT);

        EditText etItem = (EditText) findViewById(R.id.etItemText);
        etItem.setText("");
        // append method places cursor at the end of the text
        etItem.append(oldText);
    }

    public void onSubmit(View btnSubmit) {
        EditText itemText = (EditText) findViewById(R.id.etItemText);

        Intent result = new Intent();
        result.putExtra(INTENT_EXTRA_TEXT, itemText.getText().toString());
        result.putExtra(INTENT_EXTRA_INDEX, pos);

        setResult(RESULT_CODE_SUBMITTED, result);
        finish();
    }
}
