package com.cryptaur.lottery.model;

import android.content.Context;

public class TransactionStorage {
    public static final TransactionStorage INSTANCE = new TransactionStorage();

    private Context context;

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }


}
