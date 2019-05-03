package com.example.mohamedabdelazizhamad.datesofphotographer;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DatesOfPhotographer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
