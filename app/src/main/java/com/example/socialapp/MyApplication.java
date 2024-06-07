package com.example.socialapp;
// src/main/java/com/example/socialapp/MyApplication.java

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}

