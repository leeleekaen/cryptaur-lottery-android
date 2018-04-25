package com.cryptaur.lottery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.cryptaur.lottery.mytickets.MyTicketsPagerAdapter;

public class MyTicketsActivity extends ActivityBase {

    public static final String ARG_PAGE = "page";
    private final MenuHelper helper = new MenuHelper(this);
    private MyTicketsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new MyTicketsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ARG_PAGE)) {
            int page = intent.getIntExtra(ARG_PAGE, 0);
            mViewPager.setCurrentItem(page, false);
        }
        setHomeAsUp(true);
    }
}
