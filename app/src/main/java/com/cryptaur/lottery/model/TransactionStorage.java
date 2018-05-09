package com.cryptaur.lottery.model;

import android.util.Log;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.model.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class TransactionStorage {
    public static final TransactionStorage INSTANCE = new TransactionStorage();

    private static final String BOOK = "trx";

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        List<String> keys = Paper.book(BOOK).getAllKeys();
        for (String key : keys) {
            String str = Paper.book(BOOK).read(key);
            try {
                Transaction tr = Transaction.parse(new JSONObject(str));
                if (tr != null)
                    transactions.add(tr);
            } catch (Exception e) {
                Log.e(Const.TAG, e.getMessage(), e);
            }
        }
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        try {
            String str = transaction.toJson().toString();
            Paper.book(BOOK).write(transaction.transactionHash, str);
            Log.d(Const.TAG, "saving transaction: " + str);
        } catch (JSONException e) {
            Log.e(Const.TAG, e.getMessage(), e);
        }
    }

    public void removeTransaction(Transaction transaction) {
        Paper.book(BOOK).delete(transaction.transactionHash);
        try {
            Log.d(Const.TAG, "removing transaction: " + transaction.toJson().toString());
        } catch (Exception e) {
            Log.e(Const.TAG, e.getMessage(), e);
        }
    }
}
