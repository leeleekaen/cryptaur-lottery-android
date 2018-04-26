package com.cryptaur.lottery;

import android.app.Application;

import com.cryptaur.lottery.model.TransactionStorage;
import com.cryptaur.lottery.transport.SessionTransport;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class TheApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        SessionTransport.INSTANCE.initContext(getApplicationContext());
        TransactionStorage.INSTANCE.init(this);
    }
}
