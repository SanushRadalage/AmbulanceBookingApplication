package com.example.asus.ambi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class patientsign extends AppCompatActivity {

    private EditText mtxt1, mtxt2, mtxt3, mtxt4, mtxt5, mtxt6;
    private Button sign;
    String UserName, Tp, Etp;
    DatabaseReference current_user_db;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientsign);

        mAuth = FirebaseAuth.getInstance();

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(patientsign.this, patientmap.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                else
                {
                    Toast.makeText(patientsign.this, "Please enter Email and password", Toast.LENGTH_SHORT).show();

                }

            }
        };

        mtxt1 = findViewById(R.id.txt1);
        mtxt2 = findViewById(R.id.txt2);
        mtxt3 = findViewById(R.id.txt3);
        mtxt4 = findViewById(R.id.txt4);
        mtxt5 = findViewById(R.id.txt5);
        mtxt6 = findViewById(R.id.txt6);

        Animation animationl = AnimationUtils.loadAnimation(this, R.anim.left_to_rigth);
        Animation animationr = AnimationUtils.loadAnimation(this, R.anim.rigthtoleft);

        mtxt1.setAnimation(animationl);
        mtxt2.setAnimation(animationr);
        mtxt3.setAnimation(animationl);
        mtxt4.setAnimation(animationr);
        mtxt5.setAnimation(animationr);
        mtxt6.setAnimation(animationl);

        sign = findViewById(R.id.sn);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String un = mtxt1.getText().toString();
                final String cn = mtxt2.getText().toString();
                final String email = mtxt3.getText().toString();
                final String pass = mtxt4.getText().toString();
                final String bl = mtxt5.getText().toString();
                final String ec = mtxt6.getText().toString();


                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(patientsign.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(patientsign.this, "Sign up error!", Toast.LENGTH_SHORT).show();
                        } else {


                            String user_id = mAuth.getCurrentUser().getUid();
                            current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Patient").child(user_id);

                           HashMap map = new HashMap();
                           map.put("Name", un);
                           map.put("ContactNumber", cn);
                           map.put("EmergencyContact", ec);
                           map.put("BloodGroup", bl);

                            current_user_db.updateChildren(map);

                            Intent intent = new Intent(patientsign.this, patientmap.class);
                            startActivity(intent);
                            //finish();
                            return;
                        }
                    }
                });
            }
        });
     }
    }

