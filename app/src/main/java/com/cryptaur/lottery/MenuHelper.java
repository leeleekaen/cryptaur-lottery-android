package com.cryptaur.lottery;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cryptaur.lottery.model.CurrentDrawsKeeper;
import com.cryptaur.lottery.model.DrawTicketsKeeper;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.DrawIds;
import com.cryptaur.lottery.util.Drawables;

import java.util.Locale;

public class MenuHelper implements DrawTicketsKeeper.OnLatestDrawsTicketsUpdatedListener, CurrentDrawsKeeper.OnPlayedDrawsChangedListener {

    private final Activity activity;


    public MenuHelper(Activity activity) {
        this.activity = activity;
    }

    public void onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        //CurrentDraws currentDraws = Keeper.getInstance(activity).currentDrawsKeeper.getCurrentDraws();

        int unshownTicketsAmount = Keeper.getInstance(activity).drawTicketsKeeper.getUnshownTicketsCount();

        MenuItem item = menu.findItem(R.id.menuItem_uncheckedTickets);


        if (unshownTicketsAmount > 0) {
            String amountStr = unshownTicketsAmount < 10 ?
                    String.format(Locale.getDefault(), "%d", unshownTicketsAmount)
                    : "9+";
            Drawable drw = Drawables.createDrawableWithText(activity.getResources(), amountStr, R.drawable.ic_badge, Color.WHITE);
            item.setIcon(drw);
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
    }

    public void onActivityResume() {
        Keeper.getInstance(activity).drawTicketsKeeper.addListener(this);
        Keeper.getInstance(activity).currentDrawsKeeper.addOnPlayedDrawsChangedListener(this);
        activity.invalidateOptionsMenu();
    }

    public void onActivityPause() {
        Keeper.getInstance(activity).drawTicketsKeeper.removeListener(this);
        Keeper.getInstance(activity).currentDrawsKeeper.removeOnPlayedDrawsChangedListener(this);
    }

    @Override
    public void onLatestDrawsTicketsUpdated(CurrentDraws currentDraws) {
        activity.invalidateOptionsMenu();
    }

    @Override
    public void onPlayedDrawsChanged(DrawIds oldPlayedDrawIds, DrawIds newPlayedDrawIds, CurrentDraws currentDraws) {
        activity.invalidateOptionsMenu();
    }
}
