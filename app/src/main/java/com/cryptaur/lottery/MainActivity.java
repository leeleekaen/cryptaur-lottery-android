package com.cryptaur.lottery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cryptaur.lottery.adapter.MainLotteryPagerAdapter;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements GetObjectCallback<CurrentDraws> {

    private final MenuHelper helper = new MenuHelper(this);
    private Toolbar toolbar;
    private ViewPager lotteryPager;
    private TextView prizePool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        lotteryPager = findViewById(R.id.lotteryPager);
        prizePool = findViewById(R.id.prizePool);

        MainLotteryPagerAdapter adapter = new MainLotteryPagerAdapter(Lottery.values());
        lotteryPager.setAdapter(adapter);

        float density = getResources().getDisplayMetrics().density;
        lotteryPager.setPageMargin((int) (density * 8));

        Keeper.getInstance(this).getCurrentDraws(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        helper.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        helper.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
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

    @Override
    public void onRequestResult(CurrentDraws responce) {
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
}
