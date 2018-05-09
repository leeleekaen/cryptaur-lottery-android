package com.cryptaur.lottery.transport;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.cryptaur.lottery.model.TransactionKeeper;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.DrawsReply;
import com.cryptaur.lottery.transport.model.DrawsRequest;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.model.TransactionState;
import com.cryptaur.lottery.transport.model.WinTicketReply;
import com.cryptaur.lottery.transport.model.WinTicketsRequest;
import com.cryptaur.lottery.transport.request.BuyTicketRequest;
import com.cryptaur.lottery.transport.request.CheckTransactionStateRequest;
import com.cryptaur.lottery.transport.request.GetBalanceRequest;
import com.cryptaur.lottery.transport.request.GetCurrentLotteriesRequest;
import com.cryptaur.lottery.transport.request.GetDrawsRequest;
import com.cryptaur.lottery.transport.request.GetTheWinRequest;
import com.cryptaur.lottery.transport.request.GetTicketPriceRequest;
import com.cryptaur.lottery.transport.request.GetTicketsRequest;
import com.cryptaur.lottery.transport.request.GetWinAmountRequest;
import com.cryptaur.lottery.transport.request.GetWinTicketsRequest;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;

public class Transport {

    public static final Transport INSTANCE = new Transport();
    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicInteger requestCounter = new AtomicInteger();

    public void getLotteries(@Nullable NetworkRequest.NetworkRequestListener<CurrentDraws> listener) {
        new GetCurrentLotteriesRequest(client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void buyTicket(Ticket ticket, NetworkRequest.NetworkRequestListener<Transaction> listener) {
        new BuyTicketRequest(client, ticket, SessionTransport.INSTANCE.getCurrentSession(), new TransactionRequestWrapper<>(listener)).execute();
    }

    public void getBalance(NetworkRequest.NetworkRequestListener<BigInteger> listener) {
        String address = SessionTransport.INSTANCE.getAddress();
        if (address == null)
            listener.onCancel(null);
        else
            new GetBalanceRequest(address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public boolean canAuthorizeWithPin() {
        return SessionTransport.INSTANCE.canAuthorizeWithPin();
    }

    public void getTicketFee(Draw draw, NetworkRequest.NetworkRequestListener<Money> listener) {
        String address = SessionTransport.INSTANCE.getAddress();
        if (address == null) {
            listener.onCancel(new GetTicketPriceRequest(draw, address, client, listener));
            return;
        }
        new GetTicketPriceRequest(draw, address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getTickets(TicketsToLoad toLoad, NetworkRequest.NetworkRequestListener<LotteryTicketsList> listener) {
        String address = SessionTransport.INSTANCE.getAddress();
        if (address == null) {
            listener.onCancel(new GetTicketsRequest(toLoad, null, client, listener));
            return;
        }
        new GetTicketsRequest(toLoad, address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getDraws(DrawsRequest request, NetworkRequest.NetworkRequestListener<DrawsReply> listener) {
        new GetDrawsRequest(request, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getWinAmount(NetworkRequest.NetworkRequestListener<Money> listener) {
        String address = SessionTransport.INSTANCE.getAddress();
        if (address == null)
            listener.onCancel(null);

        new GetWinAmountRequest(address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getTheWin(Money amount, NetworkRequest.NetworkRequestListener<Transaction> listener) {
        new GetTheWinRequest(amount, SessionTransport.INSTANCE.getCurrentSession(), client, new TransactionRequestWrapper<>(listener)).execute();
    }

    public void getWinTickets(WinTicketsRequest request, NetworkRequest.NetworkRequestListener<WinTicketReply> listener) {
        new GetWinTicketsRequest(request, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getTransactionState(Transaction transaction, NetworkRequest.NetworkRequestListener<TransactionState> listener) {
        new CheckTransactionStateRequest(transaction, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    /**
     * wraps normal requests
     */
    private class NetworkRequestWrapper<T> implements NetworkRequest.NetworkRequestListener<T> {
        @Nullable
        private final NetworkRequest.NetworkRequestListener<T> callback;

        NetworkRequestWrapper(@Nullable NetworkRequest.NetworkRequestListener<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onNetworkRequestStart(NetworkRequest request) {
            requestCounter.incrementAndGet();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestStart(request));
        }

        @Override
        public void onNetworkRequestDone(NetworkRequest request, T responce) {
            requestCounter.decrementAndGet();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestDone(request, responce));
        }

        @Override
        public void onNetworkRequestError(NetworkRequest request, Exception e) {
            requestCounter.decrementAndGet();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestError(request, e));
        }

        @Override
        public void onCancel(NetworkRequest request) {
            requestCounter.decrementAndGet();
            if (callback != null)
                handler.post(() -> callback.onCancel(request));
        }
    }

    private class TransactionRequestWrapper<T extends Transaction> extends NetworkRequestWrapper<T> {
        public TransactionRequestWrapper(@Nullable NetworkRequest.NetworkRequestListener<T> callback) {
            super(callback);
        }

        @Override
        public void onNetworkRequestDone(NetworkRequest request, T responce) {
            TransactionKeeper.INSTANCE.onNewTransaction(responce);
            super.onNetworkRequestDone(request, responce);
        }
    }
}