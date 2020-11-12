package edu.iit.vidhia2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewNoteActivity extends AppCompatActivity {

    private static final String TAG = "NewNoteActivity";
    private Notes notes;

    EditText description;
    EditText title;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitynewnoteactivity);

        title = (EditText)findViewById(R.id.editTextNoteTitle);
        description = (EditText)findViewById(R.id.editTextNoteDescription);

        data = getIntent();
        notes = (Notes) data.getSerializableExtra("Notes");

        if(notes != null) {
            title.setText(notes.getTitle());
            description.setText(notes.getDescription());
        }
    }

    protected void onResume() {
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newnote_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                notes.setTitle(title.getText().toString());
                notes.setDescription(description.getText().toString());
                notes.setDate(getCurrentTime());

                data.putExtra("noteOBJ", notes);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause(){
        notes.setDate(getCurrentTime());
        super.onPause();
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notes.setTitle(title.getText().toString());
                notes.setDescription(description.getText().toString());
                notes.setDate(getCurrentTime());

                data.putExtra("noteOBJ", notes);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setMessage("Save Your Note ?");
        builder.setTitle("Your Note is Not Saved !");
        builder.create().show();
    }

    public String getCurrentTime() {
        return new SimpleDateFormat("EEE MMM  d, HH:mm a").format(Calendar.getInstance().getTime());
    }
}
