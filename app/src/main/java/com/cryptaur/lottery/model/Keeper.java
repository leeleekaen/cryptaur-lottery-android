package com.cryptaur.lottery.model;

import android.content.Context;
import android.support.annotation.UiThread;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.CurrentDraws;

public class Keeper {
    private static final long DRAWS_UPDATE_TIMEOUT = 600_000; // 10 mins

    private static volatile Keeper keeper;

    private final RequestJoiner<CurrentDraws> currentDraws = new RequestJoiner<>();

    public Keeper(Context context) {

    }

    public static Keeper getInstance(Context context) {
        Keeper local = keeper;
        if (local == null) {
            synchronized (Keeper.class) {
                local = keeper;
                if (local == null) {
                    local = keeper = new Keeper(context);
                }
            }
        }
        return local;
    }

    @UiThread
    public void getCurrentDraws(final GetObjectCallback<CurrentDraws> listener) {
        if (!currentDraws.isExecutingRequest() && currentDraws.isResultOutdated(DRAWS_UPDATE_TIMEOUT)) {
            currentDraws.setExecutingRequest(true);
            Transport.INSTANCE.getLotteries(currentDraws);
        }

        if (currentDraws.isExecutingRequest()){
            currentDraws.addCallback(listener);
        }

        CurrentDraws draws = currentDraws.getValue();
        if (draws != null) {
            listener.onRequestResult(draws);
        }
    }

}
