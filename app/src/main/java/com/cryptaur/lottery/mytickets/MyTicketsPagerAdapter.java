package com.cryptaur.lottery.mytickets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.cryptaur.lottery.transport.model.TicketsType;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MyTicketsPagerAdapter extends FragmentPagerAdapter {

    MyTicketsFragment selectedFragment;

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

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (selectedFragment != null)
            selectedFragment.setPrimary(false);
        if (object instanceof MyTicketsFragment) {
            ((MyTicketsFragment) object).setPrimary(true);
        }
    }
}
