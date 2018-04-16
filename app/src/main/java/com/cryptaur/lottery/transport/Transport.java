package com.cryptaur.lottery.transport;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.BuyTicketResponce;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.request.BaseLotteryRequest;
import com.cryptaur.lottery.transport.request.BuyTicketRequest;
import com.cryptaur.lottery.transport.request.GetCurrentLotteriesRequest;
import com.cryptaur.lottery.transport.request.LoginRequest;
import com.cryptaur.lottery.transport.request.RefreshSessionRequest;
import com.cryptaur.lottery.transport.request.SessionRequestQueue;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;

public class Transport {

    public static final Transport INSTANCE = new Transport();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicInteger requestCounter = new AtomicInteger();
    private final SessionRequestQueue sessionRequestQueue = new SessionRequestQueue();
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    private Session currentSession;

    public void getLotteries(@Nullable NetworkRequest.NetworkRequestListener listener) {
        new GetCurrentLotteriesRequest(client, new NetworkRequestWrapper(listener)).execute();
    }

    public void login(Login login, @Nullable NetworkRequest.NetworkRequestListener listener) {
        synchronized (this) {
            sessionRequestQueue.clear();
            BaseLotteryRequest request = new LoginRequest(client, login, new NetworkSessionRequestWrapper(listener));
            sessionRequestQueue.doRequest(request);
        }
    }

    public void refreshSession(@Nullable NetworkRequest.NetworkRequestListener listener) {
        synchronized (this) {
            if (currentSession == null)
                return;

            BaseLotteryRequest request = new RefreshSessionRequest(client, currentSession, new NetworkSessionRequestWrapper(listener));
            sessionRequestQueue.doRequest(request);
        }
    }

    public void buyTicket(Ticket ticket, NetworkRequest.NetworkRequestListener<BuyTicketResponce> listener) {
        currentSession = new Session("asdf", "qwer");
        new BuyTicketRequest(client, ticket, currentSession, listener).execute();
    }

    /**
     * wraps normal requests
     */
    private class NetworkRequestWrapper<T> implements NetworkRequest.NetworkRequestListener<T> {
        @Nullable
        private final NetworkRequest.NetworkRequestListener<T> callback;

        public NetworkRequestWrapper(@Nullable NetworkRequest.NetworkRequestListener<T> callback) {
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

        public NetworkSessionRequestWrapper(@Nullable NetworkRequest.NetworkRequestListener<T> callback) {
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
                    currentSession = (Session) responce;
                    sessionRequestQueue.setCurrentSession(currentSession);
                }
            }
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestDone(request, responce));
        }

        @Override
        public void onNetworkRequestError(NetworkRequest request, Exception e) {
            sessionRequestQueue.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestError(request, e));
        }

        @Override
        public void onCancel(NetworkRequest request) {
            sessionRequestQueue.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onCancel(request));
        }
    }
}