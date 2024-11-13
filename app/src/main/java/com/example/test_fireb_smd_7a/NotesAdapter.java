package com.example.test_fireb_smd_7a;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NotesAdapter extends FirebaseRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {
    Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NotesAdapter(@NonNull FirebaseRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Note model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View v = LayoutInflater.from(context).inflate(R.layout.insert_update_note_design, null, false);
                AlertDialog.Builder editNote = new AlertDialog.Builder(context)
                        .setTitle("Update Note")
                        .setView(v);

                EditText etTitle, etDescription;
                etTitle = v.findViewById(R.id.etTitle);
                etDescription = v.findViewById(R.id.etDescription);

                etTitle.setText(model.getTitle());
                etDescription.setText(model.getDescription());


                editNote.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("title", etTitle.getText().toString().trim());
                        data.put("description", etDescription.getText().toString().trim());

                        FirebaseDatabase.getInstance().getReference().child("Notes")
                                .child(getRef(position).getKey())
                                .updateChildren(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Record Updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

                editNote.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Notes")
                                .child(getRef(position).getKey())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                editNote.show();
                return false;
            }
        });
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.single_note_design, parent, false);
        return new NotesViewHolder(v);
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvDescription;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
