package com.cryptaur.lottery.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.buytickets.BuyTicketActivity;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.Strings;

import java.util.Locale;

public class LotteryViewMainViewHolder implements GetObjectCallback<CurrentDraws>, View.OnAttachStateChangeListener, View.OnClickListener {

    public final ViewGroup view;
    public final Lottery lottery;

    private final TextView drawNumberView;
    private final TextView jackpotAmountView;
    private final TextView timeLeftView;
    private final Button buyButtonView;

    private final Handler handler = new Handler();
    /**
     * a little randomize intervals to avoid request peaks at server
     */
    private final long _1_min_interval = 55_000 + Math.round(Math.random() * 20_000);
    private final long _5_min_interval = 290_000 + Math.round(Math.random() * 60_000);
    private final long _10_min_interval = 580_000 + Math.round(Math.random() * 60_000);
    private Draw draw;
    private boolean posted = false;
    private boolean attached = false;
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (attached) {
                posted = true;
                handler.postDelayed(this, 1000);
                updateTimer();
            } else {
                posted = false;
            }
        }
    };

    private LotteryViewMainViewHolder(ViewGroup view, Lottery lottery) {
        this.view = view;
        this.lottery = lottery;
        ImageView ballsView = view.findViewById(R.id.balls);
        drawNumberView = view.findViewById(R.id.drawNumber);
        jackpotAmountView = view.findViewById(R.id.jackPotAmount);
        timeLeftView = view.findViewById(R.id.timeToDraw);
        buyButtonView = view.findViewById(R.id.buyButton);

        switch (lottery) {
            case _4of20:
                ballsView.setImageResource(R.drawable.balls_4x20);
                break;
            case _5of36:
                ballsView.setImageResource(R.drawable.balls_5x36);
                break;
            case _6of42:
                ballsView.setImageResource(R.drawable.balls_6x42);
                break;
        }
        update(false);

        view.addOnAttachStateChangeListener(this);
        buyButtonView.setOnClickListener(this);
        view.setOnClickListener(this);
    }

    public static LotteryViewMainViewHolder create(ViewGroup parent, Lottery lottery) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_lottery_view, parent, false);
        return new LotteryViewMainViewHolder(view, lottery);
    }

    private void update(boolean force) {
        Keeper.INSTANCE.getCurrentDraws(this, force);
    }

    @Override
    public void onRequestResult(CurrentDraws currentDraws) {
        draw = currentDraws.getDraw(lottery);
        if (draw == null)
            return;
        String jackPotSize = CPT.toDecimalString(draw.jackpot);
        String ticketPrice = CPT.toDecimalString(draw.getTicketPrice().amount);
        Resources res = view.getResources();
        drawNumberView.setText(res.getString(R.string.draw_number, draw.number));

        jackpotAmountView.setText(view.getResources().getString(R.string._CPT, jackPotSize));
        buyButtonView.setText(res.getString(R.string.buy_ticket_for, ticketPrice));
        updateTimer();
    }

    private void updateTimer() {
        if (draw != null) {
            long secondsToDraw = draw.secondsToDraw();
            if (secondsToDraw > 0) {
                String time = Strings.formatInterval(secondsToDraw, view.getContext()).toUpperCase(Locale.getDefault());
                timeLeftView.setText(view.getResources().getString(R.string.time_left_, time));
            } else {
                timeLeftView.setText("");
                long overtime = secondsToDraw * -1000;
                if (overtime > _1_min_interval) {
                    long age = System.currentTimeMillis() - draw.timestamp;
                    if (age > overtime) {
                        update(true);
                    } else if (overtime > _5_min_interval && overtime - age < 200_000) {
                        update(true);
                    } else if (overtime > _10_min_interval && overtime - age < 400_000) {
                        update(true);
                    } else if (age > _10_min_interval) {
                        update(true);
                    }
                }
            }

            Resources res = view.getResources();
            if (secondsToDraw >= Const.STOP_TICKET_SELL_INTERVAL_SEC) {
                String ticketPrice = CPT.toDecimalString(draw.getTicketPrice().amount);
                buyButtonView.setText(res.getString(R.string.buy_ticket_for, ticketPrice));
                buyButtonView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            } else if (secondsToDraw > 0) {
                buyButtonView.setText(res.getString(R.string.ticketSaleIsOver, draw.number));
                buyButtonView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            } else {
                buyButtonView.setText(R.string.playing);
                buyButtonView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        }
    }

    @Override
    public void onNetworkRequestError(Exception e) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        handler.post(updateTimerRunnable);
        attached = true;
        Keeper.INSTANCE.currentDrawsKeeper.addListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        attached = false;
        Keeper.INSTANCE.currentDrawsKeeper.removeListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(view.getContext(), BuyTicketActivity.class);
        intent.putExtra(BuyTicketActivity.ARG_LOTTERY, lottery);
        view.getContext().startActivity(intent);
    }
}
