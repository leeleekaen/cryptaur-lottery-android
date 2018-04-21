package com.cryptaur.lottery.mytickets;

import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class GetTheWinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, GetObjectCallback<BigInteger> {

    private final ViewGroup view;
    private final TextView winAmountView;
    private final Button getTheWinButton;
    private final InteractionListener listener;

    public GetTheWinViewHolder(ViewGroup view, InteractionListener listener) {
        super(view);
        this.view = view;
        this.listener = listener;
        winAmountView = view.findViewById(R.id.winAmount);
        getTheWinButton = view.findViewById(R.id.getTheWinButton);
        getTheWinButton.setOnClickListener(this);
        Keeper.getInstance(view.getContext()).getUnusedWin(this);

        View bgView = view.findViewById(R.id.bgView);
        Drawable dr = VectorDrawableCompat.create(view.getResources(), R.drawable.bg_sparks, null);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        bgView.setBackground(dr);
    }

    public static GetTheWinViewHolder create(ViewGroup parent, InteractionListener listener) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_get_the_win, parent, false);
        return new GetTheWinViewHolder(view, listener);
    }

    @Override
    public void onClick(View v) {
        listener.doAction(InteractionListener.Action.GetTheWin, null);
    }

    @Override
    public void onRequestResult(BigInteger responce) {
        String amount = Strings.toDecimalString(responce, 8, 0, ".", ",");
        amount = view.getResources().getString(R.string._CPT, amount);
        winAmountView.setText(amount);
    }

    @Override
    public void onNetworkRequestError(Exception e) {

    }

    @Override
    public void onCancel() {

    }
}
