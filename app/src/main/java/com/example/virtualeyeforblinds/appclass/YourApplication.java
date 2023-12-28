package com.example.virtualeyeforblinds.appclass;

import android.app.Application;

import com.example.virtualeyeforblinds.extraClasses.NetworkInitializer;
import com.example.virtualeyeforblinds.globalClass.DataStorage;

public class YourApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();



            NetworkInitializer.initialize(getApplicationContext());


        // Initialize your application-wide components here

    }
}
