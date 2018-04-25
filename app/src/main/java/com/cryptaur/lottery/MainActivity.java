package com.cryptaur.lottery;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.cryptaur.lottery.adapter.MainLotteryPagerAdapter;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class MainActivity extends ActivityBase {

    private ViewPager lotteryPager;
    private TextView prizePool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lotteryPager = findViewById(R.id.lotteryPager);
        prizePool = findViewById(R.id.prizePool);

        MainLotteryPagerAdapter adapter = new MainLotteryPagerAdapter(Lottery.values());
        lotteryPager.setAdapter(adapter);

        float density = getResources().getDisplayMetrics().density;
        lotteryPager.setPageMargin((int) (density * 8));

        Keeper.getInstance(this).getCurrentDraws(this, false);
    }

    @Override
    public void onRequestResult(CurrentDraws responce) {
        super.onRequestResult(responce);
        BigInteger jackpot = responce.getTotalJackpot();
        String jackpotStr = Strings.toDecimalString(jackpot, 8, 0, ".", ",");
        jackpotStr = getResources().getString(R.string._CPT, jackpotStr);
        prizePool.setText(jackpotStr);
    }

    @Override
    public void onNetworkRequestError(Exception e) {
    }

    @Override
    public void onCancel() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
