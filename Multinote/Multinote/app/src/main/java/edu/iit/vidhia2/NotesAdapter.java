package edu.iit.vidhia2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "NotesAdapter";

    private ArrayList<Notes> notesArrayList;
    private MainActivity mainActivity;

    public NotesAdapter(MainActivity mainActivity, ArrayList<Notes> notesArrayList) {
        this.notesArrayList = notesArrayList;
        this.mainActivity = mainActivity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.noteslistitem, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mainActivity.getRecyclerView().getChildAdapterPosition(view);
                mainActivity.doModify(view, position);
            }

        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View itemV) {
                try {
                    final int position = mainActivity.getRecyclerView().getChildAdapterPosition(itemV);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setMessage("Delete Note ?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                mainActivity.doMoveNote(itemV, position);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                     /*if (note.getNote_text().length() > 80){
        holder.note_text.setText(String.format("%s...", note.getNote_text().substring(0, 80)));
    }
        else {
        holder.note_text.setText(note.getNote_text());
        */

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notes notes = notesArrayList.get(position);

        holder.title.setText(notes.getTitle());
        holder.date.setText(notes.getDate());
        holder.description.setText(notes.getDescription());
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    public void notesrefresh(ArrayList<Notes> notesArrayList) {
        this.notesArrayList = notesArrayList;
        notifyDataSetChanged();
    }
}
