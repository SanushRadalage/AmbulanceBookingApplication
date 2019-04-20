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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class driversign extends AppCompatActivity {

    private EditText mtxt1, mtxt2, mtxt3, mtxt4, mtxt5;
    private String mservice;
    private Button sign;
    RadioGroup mradiogroup;
    RadioButton mradiobutton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driversign);

        mAuth = FirebaseAuth.getInstance();

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(driversign.this, Drivermap.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };

        mtxt1 = findViewById(R.id.txt1);
        mtxt2 = findViewById(R.id.txt2);
        mtxt3 = findViewById(R.id.txt3);
        mtxt4 = findViewById(R.id.txt4);

        Animation animationl = AnimationUtils.loadAnimation(this, R.anim.left_to_rigth);
        Animation animationr = AnimationUtils.loadAnimation(this, R.anim.rigthtoleft);
        //Animation upani = AnimationUtils.loadAnimation(this, R.anim.down_to_up);

        mtxt1.setAnimation(animationl);
        mtxt2.setAnimation(animationr);
        mtxt3.setAnimation(animationl);
        mtxt4.setAnimation(animationr);


        sign = findViewById(R.id.sn);
        mradiogroup = findViewById(R.id.radioGroup);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String un = mtxt1.getText().toString();
                final String cn = mtxt2.getText().toString();
                final String  email= mtxt3.getText().toString();
                final String pass = mtxt4.getText().toString();

                int radioId = mradiogroup.getCheckedRadioButtonId();
                mradiobutton = findViewById(radioId);

                mservice = mradiobutton.getText().toString();

                    switch (mservice) {
                        case "Hemas":
                            mradiogroup.check(R.id.ambiX);
                            break;

                        case "Nawaloka":
                            mradiogroup.check(R.id.ambiY);
                            break;

                        case "Durdans":
                            mradiogroup.check(R.id.ambiZ);
                            break;

                        case "1990":
                            mradiogroup.check(R.id.ambiA);
                            break;

                    }



                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(driversign.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(driversign.this, "Sign up error!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DatabaseReference current_user_db;
                            String user_id = mAuth.getCurrentUser().getUid();
                            current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(user_id);

                            HashMap map = new HashMap();
                            map.put("Name", un);
                            map.put("Contact_Number", cn);
                            map.put("Service",mservice);
                            current_user_db.updateChildren(map);

                            //current_user_db.setValue(cn);

                            Intent intent = new Intent(driversign.this, Drivermap.class);
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

