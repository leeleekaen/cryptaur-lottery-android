package com.cryptaur.lottery.transport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

import static com.cryptaur.lottery.Const.KEY_ADDRESS;
import static com.cryptaur.lottery.transport.base.NetworkRequest.TAG;

public class SessionStorage {

    private static final String KEY_ALIAS = "lotteryKeyAlias";
    private static final String KEY_DEVICE_ID = "name";
    private static final String KEY_USER_NAME = "name";
    private Context context;

    public SessionStorage(Context context) {
        this.context = context;
    }

    public void initContext(Context context) {
        this.context = context.getApplicationContext();
    }


    public String getDeviceId() {
        SharedPreferences preferences = context.getSharedPreferences("device", Context.MODE_PRIVATE);
        if (preferences.contains(KEY_DEVICE_ID)) {
            return preferences.getString(KEY_DEVICE_ID, null);
        } else {
            String value = UUID.randomUUID().toString();
            preferences.edit().putString(KEY_DEVICE_ID, value).apply();
            return value;
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

    public String getAddress() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_ADDRESS, null);
    }

    public boolean canAuthorizeWithPin() {
        return context.getSharedPreferences("device", Context.MODE_PRIVATE).contains(KEY_DEVICE_ID)
                && PreferenceManager.getDefaultSharedPreferences(context).contains(KEY_USER_NAME);
    }

    public String getUsername() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USER_NAME, null);
    }

    public void saveLogin(Login login, Session session) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(KEY_USER_NAME, login.login.toString())
                .putString(KEY_ADDRESS, session.address)
                .apply();
    }

    public void clear() {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .remove(KEY_USER_NAME)
                .remove(KEY_ADDRESS)
                .apply();
    }
}
