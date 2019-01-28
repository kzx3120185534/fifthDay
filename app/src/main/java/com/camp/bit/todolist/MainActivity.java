package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.Priority;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        //初始化db
        mDbHelper = new TodoDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        notesAdapter.refresh(loadNotesFromDatabase());


    }

    @Override
    protected void onDestroy() {
        if(mDbHelper!=null)
            mDbHelper.close();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans

        if( mDb == null)
            return Collections.emptyList();

        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = null;
        try{

            //query for results which order by priority in descend sequence
            cursor = mDb.query(TodoContract.TodoEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    TodoContract.TodoEntry.PRIORITY + " desc");


            while(cursor.moveToNext()){
                Note note = new Note(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.CONTENT)));
                note.setDate(new Date(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.DATE))));
                note.setState(State.from(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.STATE))));
                note.setPriority(Priority.from(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.PRIORITY))));
                notes.add(note);
            }

        }finally {
            //close cursor
            if(cursor!=null)
                cursor.close();
        }
        return notes;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        mDb.delete(TodoContract.TodoEntry.TABLE_NAME,
                TodoContract.TodoEntry.ID + " = ?",new String[]{Long.toString(note.id)});

        //update ui after delete
        notesAdapter.refresh(loadNotesFromDatabase());

    }

    private void updateNode(Note note) {
        ContentValues cv = new ContentValues();
        //update specific note state
        cv.put(TodoContract.TodoEntry.STATE,note.getState().intValue);
        mDb.update(TodoContract.TodoEntry.TABLE_NAME,cv,
                TodoContract.TodoEntry.ID + " = ?",new String[]{Long.toString(note.id)});

        //update ui after
        notesAdapter.refresh(loadNotesFromDatabase());
    }

}
