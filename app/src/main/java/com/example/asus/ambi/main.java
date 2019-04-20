package com.example.asus.ambi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class main extends AppCompatActivity {

    private Button patient, driver, emerg;
    ImageView ambuimage, hospi;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        patient = findViewById(R.id.p);
        driver = findViewById(R.id.d);
        emerg = findViewById(R.id.e);
        ambuimage = findViewById(R.id.ambImage);
        hospi = findViewById(R.id.hospi);


        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.left_to_rigth);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.bounce);

        ambuimage.setAnimation(animation1);
        hospi.setAnimation(animation2);


        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(main.this, driverlog.class);
                startActivity(intent);
                patient.setVisibility(View.INVISIBLE);
                //finish();
                return;
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main.this, patientlog.class);
                startActivity(intent);
                driver.setVisibility(View.INVISIBLE);
                //finish();
                return;
            }
        });

        emerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = "emergency@gmail.com";
                final String pass = "123456";
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(main.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Intent intent = new Intent(main.this, emergencymap.class);
                        startActivity(intent);
                        return;
                    }
                });
            }

        });


    }
}