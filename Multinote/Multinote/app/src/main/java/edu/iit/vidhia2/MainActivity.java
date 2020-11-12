package edu.iit.vidhia2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;


import android.os.Bundle;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NEWNOTE_REQUEST_CODE = 1;
    private static final int MODIFY_REQUEST_CODE = 2;

    private ArrayList<Notes> notesArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter noteAdapter;
    private int position = -1;
    private Notes notes;

    public void updateTitleCount() {
        int totalNotes = notesArrayList.size();
        if (totalNotes != 0)
            setTitle("Multi Note" + " (" + totalNotes + ")");
        else
            setTitle("Multi Note");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotesHolder);
        noteAdapter = new NotesAdapter(this, notesArrayList);
        recyclerView.setAdapter(noteAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayout);

//        Load the previous saved notes or create a new file
        Task Task = new Task(this);
        Task.execute(getString(R.string.filename));
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    public void fetchAsyncTaskData(ArrayList<Notes> arrayList) {
        notesArrayList = arrayList;
        noteAdapter.notesrefresh(notesArrayList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_menu:
                Intent infoIntent = new Intent(this, Information_Activity.class);
                startActivity(infoIntent);
                break;
            case R.id.add_menu:
                Intent addNoteIntent = new Intent(this, NewNoteActivity.class);
                notes = new Notes();
                addNoteIntent.putExtra("Notes", notes);
                startActivityForResult(addNoteIntent, NEWNOTE_REQUEST_CODE);
                break;
        }
        return true;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEWNOTE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                notes = (Notes)data.getSerializableExtra("noteOBJ");

                try {
                    if(notes.getTitle().equals("")) {
                        Toast.makeText(this,"Un-titled activity was not saved!", Toast.LENGTH_SHORT).show();
                    } else{
                        saveNotes(notesArrayList,false,false);}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                noteAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == MODIFY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                notes = (Notes)data.getSerializableExtra("noteOBJ");

                try {
                    notesArrayList.set(this.position, notes);
                    saveNotes(notesArrayList,true,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                noteAdapter.notifyDataSetChanged();
            }
        }
    }



    private void saveNotes(ArrayList<Notes> notesList, Boolean isDelete, Boolean isNoteEdited) throws IOException {
        try {
            if(notesList != null) {
                notesArrayList = notesList;
            }

            if (notes != null && (!isDelete || !isNoteEdited)) {
                notesArrayList.add(notes);
            }

            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, getString(R.string.encoding)));
            StringWriter stringWriter = new StringWriter();
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();

            for (int i = 0; i < notesArrayList.size(); ++i) {
                jsonWriter.beginObject();
                jsonWriter.name("title").value((notesArrayList.get(i).getTitle()));
                jsonWriter.name("date").value((notesArrayList.get(i).getDate()));
                jsonWriter.name("description").value(notesArrayList.get(i).getDescription());
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
            jsonWriter.close();

            jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();

            for (int i = 0; i < notesArrayList.size(); ++i) {
                jsonWriter.beginObject();
                jsonWriter.name("title").value((notesArrayList.get(i).getTitle()));
                jsonWriter.name("date").value((notesArrayList.get(i).getDate()));
                jsonWriter.name("description").value(notesArrayList.get(i).getDescription());
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
            jsonWriter.close();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doMoveNote(View view, int position) throws IOException {
        notesArrayList.remove(position);
        saveNotes(notesArrayList,true,true);
        noteAdapter.notifyDataSetChanged();
    }

    public void doModify(View view, int position) {
        notes = notesArrayList.get(position);
        this.position = position;
        Intent intent = new Intent(this, NewNoteActivity.class);
        intent.putExtra("Notes", notes);
        startActivityForResult(intent, MODIFY_REQUEST_CODE);
    }

    @Override
    protected void onPause() {
        try {
            saveNotes(notesArrayList,true,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

}