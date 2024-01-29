package com.my.greentoon.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.greentoon.Adapter.ToonAdapter;
import com.my.greentoon.Model.Toon;
import com.my.greentoon.R;

import java.util.ArrayList;
import java.util.List;

public class ToonListActivity extends AppCompatActivity implements ToonAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ToonAdapter toonAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private List<Toon> toonList;

    private DatabaseReference toonRefToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Redirect to login or handle the case when the user is not authenticated
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toonList = new ArrayList<>();
        toonAdapter = new ToonAdapter(toonList, this);
        recyclerView.setAdapter(toonAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Toon").child(user.getUid());

        // Listen for changes in Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toonList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Toon toon = dataSnapshot.getValue(Toon.class);
                    if (toon != null) {
                        toonList.add(toon);
                    }
                }
                toonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ToonListActivity.this, "Error loading toons: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Toon toon) {
        // Open a dialog with Edit and Delete options
        showOptionsDialog(toon);
    }

    @Override
    public void onDeleteClick(Toon toon) {
        // Set the toonRefToDelete to the correct DatabaseReference
        toonRefToDelete = FirebaseDatabase.getInstance().getReference("Toon").child(mAuth.getUid()).child(toon.getStoryName());

        // Show a confirmation dialog before deleting the Toon
        showDeleteConfirmationDialog(toon);
    }

    private void showOptionsDialog(final Toon toon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open EditToonActivity with the selected Toon object
                Intent intent = new Intent(ToonListActivity.this, EditToonActivity.class);
                intent.putExtra("toon", toon);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Set the toonRefToDelete to the correct DatabaseReference
                toonRefToDelete = FirebaseDatabase.getInstance().getReference("Toon").child(mAuth.getUid()).child(toon.getStoryName());

                // Show a confirmation dialog before deleting the Toon
                showDeleteConfirmationDialog(toon);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showDeleteConfirmationDialog(final Toon toon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Toon");
        builder.setMessage("Are you sure you want to delete this toon?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the Toon from the database
                if (toonRefToDelete != null) {
                    toonRefToDelete.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ToonListActivity.this, "Toon deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ToonListActivity.this, "Failed to delete Toon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
