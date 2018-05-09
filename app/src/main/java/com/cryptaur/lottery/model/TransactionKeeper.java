package com.cryptaur.lottery.model;

import android.os.Handler;
import android.os.Looper;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.model.TransactionState;
import com.cryptaur.lottery.util.PeriodicTask;

import java.util.List;

public class TransactionKeeper {
    public static final TransactionKeeper INSTANCE = new TransactionKeeper();
    private ActivityBase activity;
    private NetworkRequest.NetworkRequestListener<TransactionState> listener = new NetworkRequest.NetworkRequestListener<TransactionState>() {
        @Override
        public void onNetworkRequestStart(NetworkRequest request) {
        }

        @Override
        public void onNetworkRequestDone(NetworkRequest request, TransactionState responce) {
            switch (responce.state) {
                case Fail:
                    if (activity != null) {
                        activity.doAction(new ActionShowTransactionFailed(responce.transaction), null);
                        TransactionStorage.INSTANCE.removeTransaction(responce.transaction);
                    }
                    break;

                case Success:
                    TransactionStorage.INSTANCE.removeTransaction(responce.transaction);
                    break;

                case Pending:
                    break;
            }
        }

        @Override
        public void onNetworkRequestError(NetworkRequest request, Exception e) {
        }

        @Override
        public void onCancel(NetworkRequest request) {
        }
    };
    private final PeriodicTask updateTask = new PeriodicTask(new Handler(Looper.getMainLooper()), 30_000, true, this::doUpdateTransactions);

    public void onNewTransaction(Transaction transaction) {
        TransactionStorage.INSTANCE.addTransaction(transaction);
        if (!updateTask.isRunning()) {
            updateTask.scheduleToRun();
        }
    }

    public void init() {
        List<Transaction> transactions = TransactionStorage.INSTANCE.getTransactions();
        if (transactions.size() > 0)
            updateTask.setShouldRun(true);
    }

    public void onActivityResume(ActivityBase activity) {
        this.activity = activity;
        updateTask.setCanRun(true);
        updateTask.updateRunState();
    }

    public void onActivityPause() {
        this.activity = null;
        updateTask.setCanRun(false);
    }

    private void doUpdateTransactions() {
        List<Transaction> transactions = TransactionStorage.INSTANCE.getTransactions();
        if (transactions.size() == 0) {
            updateTask.setShouldRun(false);
        } else {
            for (Transaction transaction : transactions) {
                Transport.INSTANCE.getTransactionState(transaction, listener);
            }
        }
    }
}