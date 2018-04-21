package com.cryptaur.lottery.mytickets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cryptaur.lottery.transport.model.TicketsType;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MyTicketsPagerAdapter extends FragmentPagerAdapter {

    public MyTicketsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MyTicketsFragment.newInstance(TicketsType.values()[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
