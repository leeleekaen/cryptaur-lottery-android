package com.cryptaur.lottery.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsToLoad;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.request.BaseLotteryRequest;
import com.cryptaur.lottery.transport.request.BuyTicketRequest;
import com.cryptaur.lottery.transport.request.GetBalanceRequest;
import com.cryptaur.lottery.transport.request.GetCurrentLotteriesRequest;
import com.cryptaur.lottery.transport.request.GetTheWinRequest;
import com.cryptaur.lottery.transport.request.GetTicketPriceRequest;
import com.cryptaur.lottery.transport.request.GetTicketsRequest;
import com.cryptaur.lottery.transport.request.GetWinAmountRequest;
import com.cryptaur.lottery.transport.request.LoginRequest;
import com.cryptaur.lottery.transport.request.RefreshSessionRequest;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;

import static com.cryptaur.lottery.transport.base.NetworkRequest.TAG;

public class Transport implements SessionRefresher.RefresherListener {

    public static final Transport INSTANCE = new Transport();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicInteger requestCounter = new AtomicInteger();
    private final SessionTransport sessionTransport = new SessionTransport();
    private final OkHttpClient client = new OkHttpClient();
    private final SessionRefresher sessionRefresher = new SessionRefresher(handler, this);

    public void initContext(Context context) {
        sessionTransport.initContext(context);
    }

    public void getLotteries(@Nullable NetworkRequest.NetworkRequestListener<CurrentDraws> listener) {
        new GetCurrentLotteriesRequest(client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public boolean isLoggedIn() {
        return sessionTransport.isLoggedIn();
    }

    public void onResumeActivity() {
        sessionRefresher.onResumeActivity();
    }

    public void onPauseActivity() {
        sessionRefresher.onPauseActivity();
    }

    public void login(Context context, Login login, @Nullable NetworkRequest.NetworkRequestListener<Session> listener) {
        synchronized (this) {
            sessionTransport.clear();
            String deviceId = sessionTransport.getDeviceId(context);
            BaseLotteryRequest request = new LoginRequest(context, client, login, deviceId, new NetworkSessionRequestWrapper<>(listener));
            sessionTransport.doRequest(request);
        }
    }

    public void login(Context context, String pin, @Nullable NetworkRequest.NetworkRequestListener<Session> listener) {
        synchronized (this) {
            sessionTransport.clear();
            String deviceId = sessionTransport.getDeviceId(context);
            String username = sessionTransport.getUsername(context);
            Login login = new Login(username, null, pin);
            BaseLotteryRequest request = new LoginRequest(context, client, login, deviceId, new NetworkSessionRequestWrapper<>(listener));
            sessionTransport.doRequest(request);
        }
    }

    @Override
    public void refreshSession() {
        Log.d(TAG, "refresh session1");
        synchronized (this) {
            if (sessionTransport.getCurrentSession() == null)
                return;

            Log.d(TAG, "refresh session2");
            BaseLotteryRequest request = new RefreshSessionRequest(client, sessionTransport.getCurrentSession(),
                    new NetworkSessionRequestWrapper<>(null));
            sessionTransport.doRequest(request);
        }
    }

    public void buyTicket(Ticket ticket, NetworkRequest.NetworkRequestListener<Transaction> listener) {
        new BuyTicketRequest(client, ticket, sessionTransport.getCurrentSession(), new NetworkRequestWrapper<>(listener)).execute();
    }


    public void getBalance(Context context, NetworkRequest.NetworkRequestListener<BigInteger> listener) {
        String address = sessionTransport.getAddress();
        if (address == null)
            listener.onCancel(null);
        else
            new GetBalanceRequest(address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public boolean canAuthorizeWithPin(Context context) {
        return sessionTransport.canAuthorizeWithPin(context);
    }

    public void getTicketFee(Lottery lottery, NetworkRequest.NetworkRequestListener<Money> listener) {
        new GetTicketPriceRequest(lottery, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getTickets(TicketsToLoad toLoad, NetworkRequest.NetworkRequestListener<LotteryTicketsList> listener) {
        String address = sessionTransport.getAddress();
        if (address == null) {
            listener.onCancel(new GetTicketsRequest(toLoad, address, client, listener));
            return;
        }
        new GetTicketsRequest(toLoad, address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getWinAmount(NetworkRequest.NetworkRequestListener<Money> listener) {
        String address = sessionTransport.getAddress();
        if (address == null)
            listener.onCancel(null);

        new GetWinAmountRequest(address, client, new NetworkRequestWrapper<>(listener)).execute();
    }

    public void getTheWin(Money amount, NetworkRequest.NetworkRequestListener<Transaction> listener) {
        new GetTheWinRequest(amount, sessionTransport.getCurrentSession(), client, new NetworkRequestWrapper<>(listener)).execute();
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

    /**
     * wraps session requests -- requests that change or use sessions
     */
    private class NetworkSessionRequestWrapper<T> implements NetworkRequest.NetworkRequestListener<T> {
        @Nullable
        private final NetworkRequest.NetworkRequestListener<T> callback;

        NetworkSessionRequestWrapper(@Nullable NetworkRequest.NetworkRequestListener<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onNetworkRequestStart(NetworkRequest request) {
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestStart(request));
        }

        @Override
        public void onNetworkRequestDone(NetworkRequest request, T responce) {
            synchronized (Transport.this) {
                if (responce instanceof Session) {
                    sessionTransport.onSessionRequestFinishedOk(request, (Session) responce);
                    sessionRefresher.postponeRefresh((Session) responce);
                }
            }
            sessionTransport.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestDone(request, responce));
        }

        @Override
        public void onNetworkRequestError(NetworkRequest request, Exception e) {
            sessionTransport.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestError(request, e));
        }

        @Override
        public void onCancel(NetworkRequest request) {
            sessionTransport.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onCancel(request));
        }
    }
}