package com.example.asus.ambi;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Queue;

public class viewer extends AppCompatActivity {

    TextView t1, t2, t3, t4;

    public String s1, s2, s3, s4;
    private String tipId;

    FirebaseAuth aAuth;
    DatabaseReference t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        t1 = findViewById(R.id.s1);
        t2 = findViewById(R.id.s2);
        t3 = findViewById(R.id.s3);
        t4 = findViewById(R.id.s4);


        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.left_to_rigth);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.rigthtoleft);

        t1.setAnimation(animation1);
        t2.setAnimation(animation2);
        t3.setAnimation(animation1);
        t4.setAnimation(animation2);


        aAuth = FirebaseAuth.getInstance();

        tipId = aAuth.getCurrentUser().getUid();
        t = FirebaseDatabase.getInstance().getReference().child("Tips").child(tipId);
        getTipinfo();


    }

     void getTipinfo()
     {
         t.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                     Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                     if (map.get("fst") != null)
                     {
                         s1 = map.get("fst").toString();
                         t1.setText(s1);
                     }

                     if (map.get("snd") != null)
                     {
                         s2 = map.get("snd").toString();
                         t2.setText(s2);
                     }

                     if (map.get("thd") != null)
                     {
                         s3 = map.get("thd").toString();
                         t3.setText(s3);
                     }

                     if (map.get("fth") != null)
                     {
                         s4 = map.get("fth").toString();
                         t4.setText(s4);
                     }
                     else
                     {
                         t4.setVisibility(View.INVISIBLE);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }


}

