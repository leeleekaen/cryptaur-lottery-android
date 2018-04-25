package com.cryptaur.lottery.buytickets;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.WinTicket;
import com.cryptaur.lottery.util.Strings;

public class WinTicketViewHolder extends RecyclerView.ViewHolder {

    private final TextView addressView;
    private final TextView numbersGuessedView;
    private final TextView winAmountView;
    WinTicket ticket;

    public WinTicketViewHolder(ViewGroup view) {
        super(view);
        addressView = view.findViewById(R.id.address);
        numbersGuessedView = view.findViewById(R.id.numbersGuessed);
        winAmountView = view.findViewById(R.id.winAmount);
    }

    public void setTicket(WinTicket ticket) {
        this.ticket = ticket;
        Resources res = addressView.getResources();

        String numbersGuessed = res.getString(R.string.numbers_guessed, ticket.winLevel);

        String winAmount = Strings.toDecimalString(ticket.winAmount, 8, 0, ".", ",");
        winAmount = res.getString(R.string.win_cpt, winAmount);

        addressView.setText(ticket.playerAddress);
        numbersGuessedView.setText(numbersGuessed);
        winAmountView.setText(winAmount);
    }
}