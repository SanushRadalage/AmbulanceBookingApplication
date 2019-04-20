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

public class driverlog extends AppCompatActivity {

    private EditText mtxt1, mtxt2;
    private Button log, sign;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlog);

        mAuth = FirebaseAuth.getInstance();

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(driverlog.this, Drivermap.class);
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
                /* final String email = mtxt1.getText().toString();
                 final String pass = mtxt2.getText().toString();
                 mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(driverlog.this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(driverlog.this, "Sign up error!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(user_id);
                            current_user_db.setValue(true);
                        }
                     }
                 });*/


                if ( mtxt1.getText().equals("") || mtxt2.getText().equals("") || mtxt1.getText() == null
                        || mtxt1.getText() == null ){
                    Toast.makeText(getApplicationContext(), "Please enter Password and name", Toast.LENGTH_SHORT).show();

                }


                Intent intent = new Intent(driverlog.this, driversign.class);
                startActivity(intent);
                //finish();
                return;
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mtxt1.getText().toString();
                final String pass = mtxt2.getText().toString();

                if ( mtxt1.getText().equals("") || mtxt2.getText().equals("") || mtxt1.getText() == null
                        || mtxt1.getText() == null ){
                    Toast.makeText(getApplicationContext(), "Please enter Password and name", Toast.LENGTH_SHORT).show();

                }

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(driverlog.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(driverlog.this, "Login error!", Toast.LENGTH_SHORT).show();
                        }
                        else {

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

