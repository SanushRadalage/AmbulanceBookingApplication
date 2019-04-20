package com.example.asus.ambi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class firstpage extends AppCompatActivity {

    Timer timer;

    ImageView a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        a = findViewById(R.id.ambi);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade);

        a.setAnimation(animation1);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(firstpage.this, main.class);
                startActivity(intent);
                finish();

            }
        }, 1000);


    }
}
