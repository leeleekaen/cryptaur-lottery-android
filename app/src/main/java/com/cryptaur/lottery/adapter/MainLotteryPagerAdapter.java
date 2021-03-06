package com.cryptaur.lottery.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.view.LotteryViewMainViewHolder;

public class MainLotteryPagerAdapter extends PagerAdapter {

    private final Lottery[] lotteries;

    public MainLotteryPagerAdapter(Lottery[] lotteries) {
        this.lotteries = lotteries;
    }

    @Override
    public int getCount() {
        return lotteries == null ? 0 : lotteries.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LotteryViewMainViewHolder holder = LotteryViewMainViewHolder.create(container, lotteries[position]);
        container.addView(holder.view);
        return holder;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object instanceof LotteryViewMainViewHolder && ((LotteryViewMainViewHolder) object).view == view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object object) {
        collection.removeView(((LotteryViewMainViewHolder) object).view);
    }
}
