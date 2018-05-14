package com.cryptaur.lottery.view;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.model.SimpleGetObjectCallback;
import com.cryptaur.lottery.transport.SessionTransport;

import java.math.BigInteger;

public class WalletViewHolder implements SimpleGetObjectCallback<BigInteger>, View.OnClickListener, View.OnAttachStateChangeListener {
    private final TextView view;
    private final InteractionListener listener;

    public WalletViewHolder(TextView view, InteractionListener listener) {
        this.view = view;
        this.listener = listener;
        view.setOnClickListener(this);

        VectorDrawableCompat dr = VectorDrawableCompat.create(view.getResources(), R.drawable.ic_wallet, null);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        view.setCompoundDrawables(dr, null, null, null);

        TypedValue typedValue = new TypedValue();
        view.getContext().getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
        int color = view.getContext().getResources().getColor(typedValue.resourceId);

        dr.setTint(color);

        refresh(false);
        view.addOnAttachStateChangeListener(this);
    }

    public void refresh(boolean force) {
        Keeper.INSTANCE.getBalance(this, force);
    }

    @Override
    public void onRequestResult(BigInteger responce) {
        String amount = CPT.toDecimalString(responce);
        amount = view.getResources().getString(R.string._CPT, amount);
        view.setText(amount);
    }

    @Override
    public void onClick(View v) {
        if (SessionTransport.INSTANCE.isLoggedIn()) {
            refresh(true);
        } else {
            listener.doAction(InteractionListener.Action.Login, null);
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        Keeper.INSTANCE.addBalanceListener(this);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        Keeper.INSTANCE.removeBalanceListener(this);
    }
}
