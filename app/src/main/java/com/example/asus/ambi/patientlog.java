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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class patientlog extends AppCompatActivity {

    EditText mtxt1, mtxt2;
    Button log, sign;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientlog);

        mAuth = FirebaseAuth.getInstance();


            fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null)
                    {
                        Intent intent = new Intent(patientlog.this, patientmap.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }
            };




        mtxt1 = findViewById(R.id.txt1);
        mtxt2 = findViewById(R.id.txt2);


        log = findViewById(R.id.lg);
        sign = findViewById(R.id.sn);


               Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.left_to_rigth);
        Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.rigthtoleft);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.bounce);


        mtxt1.setAnimation(animation3);
        mtxt2.setAnimation(animation1);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(patientlog.this, patientsign.class);
                startActivity(intent);

            }
        });


        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mtxt1.getText().toString();
                final String pass = mtxt2.getText().toString();

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(patientlog.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(mtxt1 == null || mtxt2 == null)
                        {
                            Toast.makeText(patientlog.this, "Cannot login without Email or password", Toast.LENGTH_SHORT).show();
                        }


                        if(!task.isSuccessful())
                        {
                            Toast.makeText(patientlog.this, "Enter valid email or password!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireBaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthListener);
    }
}

