package com.cryptaur.lottery.mytickets;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.model.Lottery;

public class LastCheckedTicketIdsKeeper {

    public static int[] getLastCheckedPlayedTicketIds(Context context) {
        return getLastCheckedPlayedTicketIds(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static int[] getLastCheckedPlayedTicketIds(SharedPreferences prefs) {
        int[] ids = new int[Lottery.values().length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = prefs.getInt(Const.KEY_LATEST_PLAYED_VIEWED_INDICES + i, 0);
        }
        return ids;
    }

    public static void updateLastCheckedPlayedTicketIds(Context context, int[] newIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        boolean changed = false;
        for (int i = 0; i < newIds.length; i++) {
            String key = Const.KEY_LATEST_PLAYED_VIEWED_INDICES + i;
            if (prefs.getInt(key, 0) < newIds[i]) {
                editor.putInt(key, newIds[i]);
                changed = true;
            }
        }
        if (changed)
            editor.apply();
    }
}
