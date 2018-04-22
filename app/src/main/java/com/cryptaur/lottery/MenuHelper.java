package com.cryptaur.lottery;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.ITicketStorageRead;
import com.cryptaur.lottery.util.Drawables;

import java.util.Locale;

public class MenuHelper implements GetObjectCallback<ITicketStorageRead> {

    private final Activity activity;

    private int unshownTicketsAmount = 0;

    public MenuHelper(Activity activity) {
        this.activity = activity;
    }

    public void onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuItem_uncheckedTickets);

        //if (unshownTicketsAmount > 0) {
        String amountStr = unshownTicketsAmount < 10 ?
                String.format(Locale.getDefault(), "%d", unshownTicketsAmount)
                : "9+";
        Drawable drw = Drawables.createDrawableWithText(activity.getResources(), amountStr, R.drawable.ic_badge, Color.WHITE);
        item.setIcon(drw);
        /*} else {
            item.setVisible(false);
        }*/
    }

    @Override
    public void onRequestResult(ITicketStorageRead responce) {
        int unshownTicketsAmount = responce.getUnshownTicketsAmount(activity);
        if (this.unshownTicketsAmount != unshownTicketsAmount) {
            this.unshownTicketsAmount = unshownTicketsAmount;
            activity.invalidateOptionsMenu();
        }
    }

    @Override
    public void onNetworkRequestError(Exception e) {

    }

    @Override
    public void onCancel() {

    }
}
