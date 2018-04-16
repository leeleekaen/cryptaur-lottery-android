package com.cryptaur.lottery.buytickets;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cryptaur.lottery.transport.model.Lottery;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class BuyTicketsPagerAdapter extends FragmentPagerAdapter {

    private Lottery lottery;

    public BuyTicketsPagerAdapter(FragmentManager fm, Lottery lottery) {
        super(fm);
        this.lottery = lottery;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return BuyTicketFragment.newInstance(lottery);
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return BuyTicketActivity.PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "section " + position;
    }
}
