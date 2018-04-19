package com.cryptaur.lottery.view;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.View;
import android.widget.TextView;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class WalletViewHolder implements GetObjectCallback<BigInteger>, View.OnClickListener {
    private final TextView view;
    private final InteractionListener listener;

    public WalletViewHolder(TextView view, InteractionListener listener) {
        this.view = view;
        this.listener = listener;
        view.setOnClickListener(this);

        VectorDrawableCompat dr = VectorDrawableCompat.create(view.getResources(), R.drawable.ic_wallet, null);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        view.setCompoundDrawables(dr, null, null, null);

        refresh(false);
    }

    public void refresh(boolean force) {
        Keeper.getInstance(view.getContext()).getBalance(this, force);
    }

    @Override
    public void onRequestResult(BigInteger responce) {
        String amount = Strings.toDecimalString(responce, 8, 0, ".", ",");
        amount = view.getResources().getString(R.string._CPT, amount);
        view.setText(amount);
    }

    @Override
    public void onNetworkRequestError(Exception e) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onClick(View v) {
        if (Transport.INSTANCE.isLoggedIn()) {
            refresh(true);
        } else {
            listener.doAction(InteractionListener.Action.Login, null);
        }

    }
}
