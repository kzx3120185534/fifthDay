package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.camp.bit.todolist.beans.Priority;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private TodoDbHelper mDbHelper;
    private RadioButton rb_common;
    private RadioButton rb_high;
    private RadioButton rb_low;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        rb_high = findViewById(R.id.rb_high);
        rb_common = findViewById(R.id.rb_common);
        rb_low = findViewById(R.id.rb_low);
        //default priority of note is "common"
        rb_common.setChecked(true);

        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content) {
        //TODO 插入一条新数据，返回是否插入成功
        mDbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TodoContract.TodoEntry.CONTENT,content);
        cv.put(TodoContract.TodoEntry.DATE,System.currentTimeMillis());
        cv.put(TodoContract.TodoEntry.STATE, State.TODO.intValue);
        cv.put(TodoContract.TodoEntry.PRIORITY,getPriority());
        long insert = db.insert(TodoContract.TodoEntry.TABLE_NAME, null, cv);

        return insert != 0;
    }

    /**
     * @return get priority by selected radio button
     */
    public int getPriority() {

        if(rb_common.isChecked()){
            return Priority.COMMON.intValue;
        }

        if(rb_high.isChecked()){
            return Priority.HIGH.intValue;
        }

        if(rb_low.isChecked()){
            return Priority.LOW.intValue;
        }

        //default value
        return Priority.COMMON.intValue;
    }
}
