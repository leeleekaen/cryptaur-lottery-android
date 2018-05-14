package com.cryptaur.lottery.mytickets;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Transaction;

import java.math.BigInteger;

public class GetTheWinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, GetObjectCallback<Money>, View.OnAttachStateChangeListener {

    private final ViewGroup view;
    private final TextView winAmountView;
    private final Button getTheWinButton;
    private final InteractionListener listener;
    private final MyTicketRecyclerViewAdapter.RefreshListener refreshListener;
    private Money winAmount;

    public GetTheWinViewHolder(ViewGroup view, InteractionListener listener, MyTicketRecyclerViewAdapter.RefreshListener refreshListener) {
        super(view);
        this.view = view;
        this.listener = listener;
        winAmountView = view.findViewById(R.id.winAmount);
        getTheWinButton = view.findViewById(R.id.getTheWinButton);
        this.refreshListener = refreshListener;
        getTheWinButton.setOnClickListener(this);


        View bgView = view.findViewById(R.id.bgView);
        Drawable dr = VectorDrawableCompat.create(view.getResources(), R.drawable.bg_sparks, null);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        bgView.setBackground(dr);

        bgView.addOnAttachStateChangeListener(this);
    }

    public static GetTheWinViewHolder create(ViewGroup parent, InteractionListener listener, MyTicketRecyclerViewAdapter.RefreshListener refreshListener) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_get_the_win, parent, false);
        return new GetTheWinViewHolder(view, listener, refreshListener);
    }

    @Override
    public void onClick(View v) {
        if (!SessionTransport.INSTANCE.isLoggedIn()) {
            listener.doAction(InteractionListener.Action.Login, null);
        } else {
            Toast.makeText(v.getContext(), R.string.updatingAmountAndFee, Toast.LENGTH_SHORT).show();
            Keeper.INSTANCE.getWinAmount(new GetObjectCallback<Money>() {
                @Override
                public void onRequestResult(Money responce) {
                    winAmount = responce;
                    showGetTheWinDialog();
                }

                @Override
                public void onNetworkRequestError(Exception e) {
                    Toast.makeText(v.getContext(), R.string.errorUpdatingTicketFee, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                }
            }, true);
        }
    }

    @Override
    public void onRequestResult(Money responce) {
        this.winAmount = responce;
        String amount = CPT.toDecimalString(responce.amount);
        amount = view.getResources().getString(R.string._CPT, amount);
        winAmountView.setText(amount);
    }

    @Override
    public void onNetworkRequestError(Exception e) {
        Toast.makeText(view.getContext(), R.string.errorUpdatingTicketFee, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        Keeper.INSTANCE.getWinAmount(this, false);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    private void showGetTheWinDialog() {
        if (winAmount == null)
            return;
        StringBuilder strBuilder = new StringBuilder();
        BigInteger net = winAmount.amount.subtract(winAmount.fee);
        strBuilder.append("Total win amount: ")
                .append(CPT.toDecimalString(winAmount.amount))
                .append(" CPT\n")
                .append("Transaction fee: ")
                .append(CPT.toDecimalString(winAmount.fee))
                .append(" CPT\n")
                .append("You will get: ")
                .append(CPT.toDecimalString(net))
                .append(" CPT\n")
                .append("Proceed?");

        new AlertDialog.Builder(view.getContext())
                .setTitle("Get a win?")
                .setMessage(strBuilder)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    getTheWin();
                    dialog.dismiss();
                }).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void getTheWin() {
        if (winAmount == null)
            return;
        Transport.INSTANCE.getTheWin(winAmount, new NetworkRequest.NetworkRequestListener<Transaction>() {
            @Override
            public void onNetworkRequestStart(NetworkRequest request) {
            }

            @Override
            public void onNetworkRequestDone(NetworkRequest request, Transaction responce) {
                Toast.makeText(view.getContext(), R.string.executingGetTheWinRequest, Toast.LENGTH_SHORT).show();
                refreshListener.onRefresh();
            }

            @Override
            public void onNetworkRequestError(NetworkRequest request, Exception e) {
                Toast.makeText(view.getContext(), R.string.requestError, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(NetworkRequest request) {
                Toast.makeText(view.getContext(), R.string.requestCancelled, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
