package com.cryptaur.lottery.buytickets;

import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;

public class AvailableViewHolder implements GetObjectCallback<BigInteger> {
    private final TextView view;

    public AvailableViewHolder(TextView view) {
        this.view = view;
        refresh(false);
    }

    public void refresh(boolean force) {
        Keeper.getInstance(view.getContext()).getBalance(this, force);
    }

    @Override
    public void onRequestResult(BigInteger responce) {
        String amount = Strings.toDecimalString(responce, 8, 0, ".", ",");
        amount = view.getResources().getString(R.string.available__cpt, amount);
        view.setText(amount);
    }

    @Override
    public void onNetworkRequestError(Exception e) {
        String amount = view.getResources().getString(R.string.available__cpt, "???");
        view.setText(amount);
    }

    @Override
    public void onCancel() {
        String amount = view.getResources().getString(R.string.available__cpt, "0");
        view.setText(amount);
    }
}
