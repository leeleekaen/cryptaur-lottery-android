package com.cryptaur.lottery.buytickets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Lottery;

public class BuyTicketActivity extends ActivityBase implements TabLayout.OnTabSelectedListener {

    public static final String ARG_LOTTERY = "lottery";
    private BuyTicketsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Lottery lottery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!intent.hasExtra(ARG_LOTTERY)) {
            finish();
        }
        lottery = (Lottery) intent.getSerializableExtra(ARG_LOTTERY);
        if (lottery == null) {
            finish();
        }

        setContentView(R.layout.activity_buy_ticket);
        mSectionsPagerAdapter = new BuyTicketsPagerAdapter(getSupportFragmentManager(), lottery);

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(this);
        setHomeAsUp(true);
    }

    @Override
    public void doAction(IAction action, @Nullable Fragment fragment) {
        if (action instanceof ShowDrawDetailsAction) {
            Fragment f = DrawDetailsFragment.newInstance(((ShowDrawDetailsAction) action).draw);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragmentFrame, f);
            ft.addToBackStack("drawDetails");
            try {
                ft.commit();
            } catch (Exception e) {
                Log.e(Const.TAG, e.getMessage(), e);
            }
            return;
        }
        super.doAction(action, fragment);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0)
            fm.popBackStack();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
