package com.example.truman_cranor.doit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


public class EditItemActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_TEXT = "item_text";
    public static final String INTENT_EXTRA_INDEX = "item_pos";
    public static final String INTENT_EXTRA_COMPLETED = "old_completed";
    public static final String INTENT_EXTRA_NEW_COMPLETED = "new_completed";

    public static final int RESULT_CODE_SUBMITTED = 1;
    public static final int RESULT_CODE_ERROR = -1;

    private static final String LOG_TAG = "EditItemActivity";
    private int pos;
    private boolean oldCompleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        pos = getIntent().getIntExtra(INTENT_EXTRA_INDEX, -1);
        if (pos == -1) {
            throw new RuntimeException("Illegal pos value read in EditItemActivity:onCreate");
        }

        String oldText = getIntent().getStringExtra(INTENT_EXTRA_TEXT);
        // Use incomplete as the default value
        oldCompleted = getIntent().getBooleanExtra(INTENT_EXTRA_COMPLETED, false);

        EditText etItem = (EditText) findViewById(R.id.etItemText);
        etItem.setText("");
        // append method places cursor at the end of the text
        etItem.append(oldText);

        CheckBox completed = (CheckBox) findViewById(R.id.cbEditItemCompleted);
        completed.setChecked(oldCompleted);
    }

    public void onSubmit(View btnSubmit) {
        EditText itemText = (EditText) findViewById(R.id.etItemText);
        CheckBox completed = (CheckBox) findViewById(R.id.cbEditItemCompleted);

        Intent result = new Intent();
        result.putExtra(INTENT_EXTRA_TEXT, itemText.getText().toString());
        result.putExtra(INTENT_EXTRA_INDEX, pos);
        result.putExtra(INTENT_EXTRA_COMPLETED, oldCompleted);
        result.putExtra(INTENT_EXTRA_NEW_COMPLETED, completed.isChecked());

        setResult(RESULT_CODE_SUBMITTED, result);
        finish();
    }
}
