package com.cryptaur.lottery.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.request.BaseLotteryRequest;
import com.cryptaur.lottery.transport.request.ISessionRequest;
import com.cryptaur.lottery.transport.request.LoginRequest;
import com.cryptaur.lottery.transport.request.RefreshSessionRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.cryptaur.lottery.transport.base.NetworkRequest.TAG;

public class SessionTransport implements SessionRefresher.RefresherListener {
    public static final SessionTransport INSTANCE = new SessionTransport();

    private final Queue<NetworkRequest> requestQueue = new LinkedList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SessionRefresher sessionRefresher = new SessionRefresher(handler, this);
    private NetworkRequest currentRequest;
    private Session currentSession;
    private SessionStorage storage;


    public void onResumeActivity() {
        sessionRefresher.onResumeActivity();
    }

    public void onPauseActivity() {
        sessionRefresher.onPauseActivity();
    }

    public void initContext(Context context) {
        this.storage = new SessionStorage(context);
    }


    public void doRequest(NetworkRequest request) {
        synchronized (this) {
            if (currentRequest == null) {
                currentRequest = request;
                request.execute();
            } else {
                requestQueue.add(request);
            }
        }
    }

    public void onNetworkRequestDone() {
        synchronized (this) {
            currentRequest = requestQueue.poll();

            if (currentRequest != null) {
                if (currentRequest instanceof ISessionRequest && currentSession != null) {
                    ((ISessionRequest) currentRequest).setSession(currentSession);
                }
                currentRequest.execute();
            }
        }
    }

    public void clear() {
        List<NetworkRequest> requests = new ArrayList<>();
        synchronized (this) {
            if (currentRequest != null)
                requests.add(currentRequest);

            requests.addAll(requestQueue);
            requestQueue.clear();
        }

        for (NetworkRequest request : requests) {
            request.cancel();
        }
    }


    public void onSessionRequestFinishedOk(NetworkRequest request, Session session) {
        if (request instanceof LoginRequest) {
            LoginRequest loginRequest = (LoginRequest) request;
            Login login = loginRequest.getLogin();
            if (login != null && login.password != null && login.password.length() > 0) {
                storage.saveLogin(login, session);
            }
        }
        setCurrentSession(session);
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    private void setCurrentSession(Session currentSession) {
        synchronized (this) {
            this.currentSession = currentSession;
        }
    }

    public boolean isLoggedIn() {
        return currentSession != null && currentSession.isAlive();
    }

    public String getAddress() {
        if (currentSession != null)
            return currentSession.address;
        return storage == null ? null : storage.getAddress();
    }

    public boolean canAuthorizeWithPin() {
        return storage == null ? false : storage.canAuthorizeWithPin();
    }

    public void login(Context context, Login login, @Nullable NetworkRequest.NetworkRequestListener<Session> listener) {
        if (storage == null)
            return;

        synchronized (this) {
            SessionTransport.INSTANCE.clear();
            String deviceId = storage.getDeviceId();
            BaseLotteryRequest request = new LoginRequest(context, Transport.INSTANCE.client, login, deviceId, new SessionTransport.NetworkSessionRequestWrapper<>(listener));
            SessionTransport.INSTANCE.doRequest(request);
        }
    }

    public void login(Context context, String pin, @Nullable NetworkRequest.NetworkRequestListener<Session> listener) {
        if (storage == null)
            return;

        synchronized (this) {
            SessionTransport.INSTANCE.clear();
            String deviceId = storage.getDeviceId();
            String username = storage.getUsername();
            Login login = new Login(username, null, pin);
            BaseLotteryRequest request = new LoginRequest(context, Transport.INSTANCE.client, login, deviceId, new SessionTransport.NetworkSessionRequestWrapper<>(listener));
            SessionTransport.INSTANCE.doRequest(request);
        }
    }

    @Override
    public void refreshSession() {
        Log.d(TAG, "refresh session1");
        synchronized (this) {
            if (SessionTransport.INSTANCE.getCurrentSession() == null)
                return;

            Log.d(TAG, "refresh session2");
            BaseLotteryRequest request = new RefreshSessionRequest(Transport.INSTANCE.client, SessionTransport.INSTANCE.getCurrentSession(),
                    new SessionTransport.NetworkSessionRequestWrapper<>(null));
            SessionTransport.INSTANCE.doRequest(request);
        }
    }

    public void logout() {
        currentSession = null;
        storage.clear();
    }

    /**
     * wraps session requests -- requests that change or use sessions
     */
    public class NetworkSessionRequestWrapper<T> implements NetworkRequest.NetworkRequestListener<T> {
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
            synchronized (SessionTransport.this) {
                if (responce instanceof Session) {
                    INSTANCE.onSessionRequestFinishedOk(request, (Session) responce);
                    sessionRefresher.postponeRefresh((Session) responce);
                }
            }
            INSTANCE.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestDone(request, responce));
        }

        @Override
        public void onNetworkRequestError(NetworkRequest request, Exception e) {
            INSTANCE.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onNetworkRequestError(request, e));
        }

        @Override
        public void onCancel(NetworkRequest request) {
            INSTANCE.onNetworkRequestDone();
            if (callback != null)
                handler.post(() -> callback.onCancel(request));
        }
    }
}
