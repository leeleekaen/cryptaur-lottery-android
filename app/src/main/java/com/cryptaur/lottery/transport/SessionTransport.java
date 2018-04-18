package com.cryptaur.lottery.transport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.request.ISessionRequest;
import com.cryptaur.lottery.transport.request.LoginRequest;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import static com.cryptaur.lottery.transport.base.NetworkRequest.TAG;

public class SessionTransport {
    private static final String KEY_ALIAS = "lotteryKeyAlias";

    private static final String KEY = "name";
    NetworkRequest currentRequest;
    Queue<NetworkRequest> requestQueue = new LinkedList<>();
    Session currentSession;

    public String getDeviceId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("device", Context.MODE_PRIVATE);
        if (preferences.contains(KEY)) {
            return preferences.getString(KEY, null);
        } else {
            String value = UUID.randomUUID().toString();
            preferences.edit().putString(KEY, value).apply();
            return value;
        }
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

    private KeyPair generateKeypair() {
        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                kpg.initialize(new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .build());
                return kpg.generateKeyPair();
            } else {
                return null;
            }

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return null;
    }

    public void onSessionRequestFinishedOk(NetworkRequest request, Session session) {
        if (request instanceof LoginRequest) {
            LoginRequest loginRequest = (LoginRequest) request;
            Login login = loginRequest.getLogin();
            if (login != null && login.password != null && login.password.length() > 0) {
                PreferenceManager.getDefaultSharedPreferences(loginRequest.getContext())
                        .edit().putString(KEY, login.login.toString())
                        .putString(Const.KEY_ADDRESS, session.address)
                        .apply();
            }
        }
        setCurrentSession(session);
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        synchronized (this) {
            this.currentSession = currentSession;
        }
    }

    public boolean isLoggedIn() {
        return currentSession != null && currentSession.isAlive();
    }
}
