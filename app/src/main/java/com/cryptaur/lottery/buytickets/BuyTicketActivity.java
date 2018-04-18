package com.cryptaur.lottery.buytickets;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_buy_ticket__, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
