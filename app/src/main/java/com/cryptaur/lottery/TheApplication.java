package com.cryptaur.lottery;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class TheApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
