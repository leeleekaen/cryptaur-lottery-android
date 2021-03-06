package com.cryptaur.lottery.mytickets;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.TheApplication;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TransactionBuyTicket;
import com.cryptaur.lottery.util.PeriodicTask;
import com.cryptaur.lottery.util.Strings;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.math.BigInteger;
import java.util.Locale;

public class MyTicketViewHolder extends RecyclerView.ViewHolder implements View.OnAttachStateChangeListener {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MMM, hh:mm:ss");
    private final ViewGroup view;
    private final ImageView ballsView;
    private final TextView[] ticketNumbers = new TextView[6];
    private final TextView drawDate;
    private final TextView drawWin;
    private final TextView timeLeft;
    private final TextView drawNumber;
    private final TextView pendingTransactionView;
    private Ticket ticket;
    private final PeriodicTask updateTimeTask = new PeriodicTask(TheApplication.HANDLER, 1000, false, this::updateTimer);
    private TransactionBuyTicket ticketTransaction;

    public MyTicketViewHolder(ViewGroup view) {
        super(view);
        this.view = view;
        ballsView = view.findViewById(R.id.balls);
        drawDate = view.findViewById(R.id.drawDate);
        drawNumber = view.findViewById(R.id.drawNumber);
        drawWin = view.findViewById(R.id.drawWin);
        timeLeft = view.findViewById(R.id.timeLeft);
        pendingTransactionView = view.findViewById(R.id.pendingTransactionView);
        ticketNumbers[0] = view.findViewById(R.id.ticketNumber1);
        ticketNumbers[1] = view.findViewById(R.id.ticketNumber2);
        ticketNumbers[2] = view.findViewById(R.id.ticketNumber3);
        ticketNumbers[3] = view.findViewById(R.id.ticketNumber4);
        ticketNumbers[4] = view.findViewById(R.id.ticketNumber5);
        ticketNumbers[5] = view.findViewById(R.id.ticketNumber6);

        view.addOnAttachStateChangeListener(this);
    }

    public static MyTicketViewHolder create(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_my_ticket, parent, false);
        return new MyTicketViewHolder(view);
    }

    public void setTicketTransaction(TransactionBuyTicket ticketTransaction) {
        setTicket(ticketTransaction.ticket);
        this.ticketTransaction = ticketTransaction;
        pendingTransactionView.setVisibility(View.VISIBLE);
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        ticketTransaction = null;

        Lottery lottery = ticket.lottery;
        switch (lottery) {
            case _4of20:
                ballsView.setImageResource(R.drawable.balls_4x20_small);
                break;
            case _5of36:
                ballsView.setImageResource(R.drawable.balls_5x36_small);
                break;
            case _6of42:
                ballsView.setImageResource(R.drawable.balls_6x42_small);
                break;
        }

        drawNumber.setText(view.getResources().getString(R.string.draw_number, ticket.drawIndex));

        for (int i = 0; i < lottery.getNumbersAmount(); i++) {
            TextView view = ticketNumbers[i];
            view.setVisibility(View.VISIBLE);
            int number = ticket.numbers[i];
            String numberStr = String.format(Locale.getDefault(), "%d", Math.abs(number));
            view.setText(numberStr);
            if (ticket.isWinNumber(number)) {
                view.setBackgroundResource(R.drawable.ic_number_guessed);
            } else {
                view.setBackgroundResource(R.drawable.ic_number_selected);
            }
        }
        for (int i = lottery.getNumbersAmount(); i < 6; i++) {
            ticketNumbers[i].setVisibility(View.GONE);
        }

        if (ticket.isPlayed()) {
            if (ticket.winAmount != null && ticket.winAmount.compareTo(BigInteger.ZERO) > 0) {
                String winText = CPT.toDecimalString(ticket.winAmount);
                winText = view.getResources().getString(R.string.won_cpt, winText);
                drawWin.setText(winText);
                drawWin.setVisibility(View.VISIBLE);
            } else {
                drawWin.setVisibility(View.GONE);
            }

            String time = dateTimeFormat.format(ticket.drawDate.atZone(ZoneId.systemDefault()));
            drawDate.setText(time);
            drawDate.setVisibility(View.VISIBLE);
            timeLeft.setVisibility(View.GONE);
            updateTimeTask.setShouldRun(false);
        } else {
            drawWin.setVisibility(View.GONE);
            drawDate.setVisibility(View.GONE);
            timeLeft.setVisibility(View.VISIBLE);
            updateTimer();
            updateTimeTask.updateRunState();
        }
        pendingTransactionView.setVisibility(View.GONE);
    }

    private void updateTimer() {
        if (ticket == null) {
            updateTimeTask.setShouldRun(false);
        } else {
            long secondsToDraw = Instant.now().until(ticket.drawDate, ChronoUnit.SECONDS);
            if (secondsToDraw > 0) {
                updateTimeTask.setShouldRun(true);
                String time = Strings.formatInterval(secondsToDraw, view.getContext()).toUpperCase(Locale.getDefault());
                timeLeft.setText(view.getResources().getString(R.string.time_left_, time));
            } else {
                updateTimeTask.setShouldRun(false);
                timeLeft.setText(R.string.draw_in_progress);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        updateTimeTask.setCanRun(true);
        updateTimeTask.updateRunState();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        updateTimeTask.setCanRun(false);
    }
}
