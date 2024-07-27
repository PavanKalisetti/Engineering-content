package com.engineeringcontent.org;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SaveFirebaseData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Set persistence enabled before any other Firebase operations
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
