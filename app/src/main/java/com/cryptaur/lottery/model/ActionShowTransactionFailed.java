package com.cryptaur.lottery.model;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.transport.model.Transaction;

public class ActionShowTransactionFailed implements InteractionListener.IAction {

    public final Transaction transaction;

    public ActionShowTransactionFailed(Transaction transaction) {
        this.transaction = transaction;
    }
}
