package com.example.mohamedabdelazizhamad.datesofphotographer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.mohamedabdelazizhamad.datesofphotographer.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent i=new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(i);
                    Animatoo.animateZoom(WelcomeActivity.this);
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
