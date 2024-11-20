package com.example.test_fireb_smd_7a;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    Button btnLogout, btnVerification, btnEditProfile;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView tvUserId;
    FirebaseFirestore db;
    String uID;
    TextView tvName, tvEmail, tvPhone, tvBloodGroup;
    ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        user.reload();



        if(user==null)
        {
            startActivity(new Intent(Profile.this, Login.class));
            finish();
        }
        else
        {
            loadProfile();
            tvUserId.setText(user.getUid());

            if(user.isEmailVerified())
            {
                btnVerification.setVisibility(View.GONE);
                btnLogout.setVisibility(View.VISIBLE);
            }
            else
            {
                btnLogout.setVisibility(View.GONE);
                btnVerification.setVisibility(View.VISIBLE);
            }

        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });

        btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Profile.this, "Verify your email from your inbox", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void loadProfile() {
        db.collection("users")
                .document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            tvEmail.setText("Email : " + documentSnapshot.getString("email"));
                            if(documentSnapshot.getString("name")!=null) {
                                tvPhone.setText("Phone : " + documentSnapshot.getString("phone"));
                                tvName.setText("Name : " + documentSnapshot.getString("name"));
                                tvBloodGroup.setText("BloodGroup : " + documentSnapshot.getString("bloodgroup"));
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void init()
    {
        btnLogout = findViewById(R.id.btnLogout);
        tvUserId = findViewById(R.id.tvUserId);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        tvUserId = findViewById(R.id.tvUserId);
        btnVerification = findViewById(R.id.btnVerification);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uID = user.getUid();
        db = FirebaseFirestore.getInstance();
    }

    public void editProfile(View view)
    {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.edit_profile_dialog_view, null, false);
        EditText etName = v.findViewById(R.id.etName);
        EditText etPhone = v.findViewById(R.id.etPhone);
        EditText etBloodgroup = v.findViewById(R.id.etBloodgroup);

        db.collection("users")
                .document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {

                            if(documentSnapshot.getString("name")!=null) {
                                etPhone.setText(documentSnapshot.getString("phone"));
                                etName.setText(documentSnapshot.getString("name"));
                                etBloodgroup.setText(documentSnapshot.getString("bloodgroup"));
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog.Builder editDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(v)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        editDialog.show();
    }
}