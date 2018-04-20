package com.cryptaur.lottery.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.buytickets.BuyTicketActivity;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.Strings;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

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
    }

    public static LotteryViewMainViewHolder create(ViewGroup parent, Lottery lottery) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.main_lottery_view, parent, false);
        return new LotteryViewMainViewHolder(view, lottery);
    }

    private void update(boolean force) {
        Keeper.getInstance(view.getContext()).getCurrentDraws(this, force);
    }

    @Override
    public void onRequestResult(CurrentDraws currentDraws) {
        draw = currentDraws.getDraw(lottery);
        if (draw == null)
            return;
        String jackPotSize = Strings.toDecimalString(draw.jackpot, 8, 3, ".", ",");
        String ticketPrice = Strings.toDecimalString(draw.getTicketPrice().amount, 8, 0, ".", ",");
        Resources res = view.getResources();
        drawNumberView.setText(res.getString(R.string.draw_number, draw.number));

        jackpotAmountView.setText(view.getResources().getString(R.string._CPT, jackPotSize));
        buyButtonView.setText(res.getString(R.string.buy_ticket_for, ticketPrice));
        updateTimer();
    }

    private void updateTimer() {
        if (draw != null) {
            long secondsToDraw = Instant.now().until(draw.startTime, ChronoUnit.SECONDS);
            if (secondsToDraw > 0) {
                int hourstoDraw = (int) Math.abs(secondsToDraw / 60 / 60);
                int minutesToDraw = (int) ((Math.abs(secondsToDraw) / 60) % 60);
                int secstoDraw = (int) (Math.abs(secondsToDraw) % 60);

                String sign = secondsToDraw < 0 ? "-" : "";
                String time = String.format(Locale.US, "%s%02d:%02d:%02d", sign, hourstoDraw, minutesToDraw, secstoDraw);
                timeLeftView.setText(view.getResources().getString(R.string.time_left_, time));
            } else {
                timeLeftView.setText(R.string.draw_in_progress);
                long overtime = -secondsToDraw;
                if (overtime > _1_min_interval) {
                    long age = System.currentTimeMillis() - draw.timestamp;
                    if (age > overtime) {
                        Log.d(Const.TAG, String.format("lottery %d, update 1, age: %d, overtime: %d", draw.lottery.getServerId(), age, overtime));
                        update(true);
                    } else if (overtime > _5_min_interval && overtime - age < 100_000) {
                        Log.d(Const.TAG, String.format("lottery %d, update 2, age: %d, overtime: %d", draw.lottery.getServerId(), age, overtime));
                        update(true);
                    } else if (overtime > _10_min_interval && overtime - age < 400_000) {
                        Log.d(Const.TAG, String.format("lottery %d, update 3, age: %d, overtime: %d", draw.lottery.getServerId(), age, overtime));
                        update(true);
                    } else if (age > _10_min_interval) {
                        Log.d(Const.TAG, String.format("lottery %d, update 4, age: %d, overtime: %d", draw.lottery.getServerId(), age, overtime));
                        update(true);
                    }
                }
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
        Keeper.getInstance(view.getContext()).addCurrentDrawsListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        attached = false;
        Keeper.getInstance(view.getContext()).removeCurrentDrawsListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(view.getContext(), BuyTicketActivity.class);
        intent.putExtra(BuyTicketActivity.ARG_LOTTERY, lottery);
        view.getContext().startActivity(intent);
    }
}
