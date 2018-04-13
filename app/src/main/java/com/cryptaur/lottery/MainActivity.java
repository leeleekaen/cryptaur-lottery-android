package com.cryptaur.lottery;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cryptaur.lottery.adapter.MainLotteryPagerAdapter;
import com.cryptaur.lottery.transport.model.Lottery;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager lotteryPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        lotteryPager = findViewById(R.id.lotteryPager);

        MainLotteryPagerAdapter adapter = new MainLotteryPagerAdapter(Lottery.values());
        lotteryPager.setAdapter(adapter);

        float density = getResources().getDisplayMetrics().density;
        lotteryPager.setPageMargin((int) (density * 8));
        //lotteryPager.enableCenterLockOfChilds();
        //lotteryPager.setCurrentItemInCenter(1);
    }
}
