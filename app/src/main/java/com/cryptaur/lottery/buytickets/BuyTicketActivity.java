package com.cryptaur.lottery.buytickets;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.MyTicketsActivity;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Lottery;

public class BuyTicketActivity extends ActivityBase {

    public static final String ARG_LOTTERY = "lottery";
    private BuyTicketsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Lottery lottery;
    private Toolbar toolbar;

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

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_uncheckedTickets:
                Intent intent = new Intent(this, MyTicketsActivity.class);
                intent.putExtra(MyTicketsActivity.ARG_PAGE, 1);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
