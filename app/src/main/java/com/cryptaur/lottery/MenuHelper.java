package com.cryptaur.lottery;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.util.Drawables;

import java.util.Locale;

public class MenuHelper {

    private final Activity activity;

    public MenuHelper(Activity activity) {
        this.activity = activity;
    }

    public void onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuItem_uncheckedTickets);
        int amount = Keeper.getInstance(activity).getUncheckedTicketsAmount();
        if (amount > 0) {
            String amountStr = amount < 10 ?
                    String.format(Locale.getDefault(), "%d", amount)
                    : "9+";
            Drawable drw = Drawables.createDrawableWithText(activity.getResources(), amountStr, R.drawable.ic_badge, Color.WHITE);
            item.setIcon(drw);
        } else {
            item.setVisible(false);
        }
    }
}
