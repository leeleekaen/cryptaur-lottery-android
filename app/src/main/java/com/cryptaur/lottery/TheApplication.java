package com.cryptaur.lottery;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.cryptaur.lottery.model.TransactionKeeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.jakewharton.threetenabp.AndroidThreeTen;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;

public class TheApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        AndroidThreeTen.init(this);
        SessionTransport.INSTANCE.initContext(getApplicationContext());
        Paper.init(this);
        TransactionKeeper.INSTANCE.init();
    }
}
