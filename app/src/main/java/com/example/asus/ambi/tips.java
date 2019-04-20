package com.example.asus.ambi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class tips extends AppCompatActivity {


    RelativeLayout r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        r1 = findViewById(R.id.l1);
        r2 = findViewById(R.id.l2);
        r3 = findViewById(R.id.l3);
        r4 = findViewById(R.id.l4);
        r5 = findViewById(R.id.l5);
        r6 = findViewById(R.id.l6);
        r7 = findViewById(R.id.l7);
        r8 = findViewById(R.id.l8);
        r9 = findViewById(R.id.l9);
        r10 = findViewById(R.id.l10);
        r11 = findViewById(R.id.l11);
        r12 = findViewById(R.id.l12);


        mAuth = FirebaseAuth.getInstance();

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {

                }
            }
        };



        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = "t1@gmail.com";
                final String pass = "123456";
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(tips.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            Intent intent = new Intent(tips.this, viewer.class);
                            startActivity(intent);
                            return;
                        }


                    }
                });
            }

        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = "t2@gmail.com";
                final String pass = "123456";
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(tips.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            Intent intent = new Intent(tips.this, viewer.class);
                            startActivity(intent);
                            return;
                        }


                    }
                });

            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = "t3@gmail.com";
                final String pass = "123456";
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(tips.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful())
                        {
                            Intent intent = new Intent(tips.this, viewer.class);
                            startActivity(intent);
                            return;
                        }


                    }
                });

            }
        });

        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        r11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        r12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
