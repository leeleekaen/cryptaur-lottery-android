package com.cryptaur.lottery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.exception.ServerException;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.view.LoadingViewHolder;

import java.math.BigInteger;

public class SplashActivity extends Activity {

    LoadingViewHolder loadingViewHolder;
    boolean errorVisible;
    private boolean drawsLoaded;
    private boolean balanceLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadingViewHolder = new LoadingViewHolder(findViewById(R.id.viewLoading));
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        loadingViewHolder.setRunAnimation(true);
        Keeper.INSTANCE.getCurrentDraws(new GetObjectCallback<CurrentDraws>() {
            @Override
            public void onRequestResult(CurrentDraws responce) {
                drawsLoaded = true;
                SplashActivity.this.checkContinue();
            }

            @Override
            public void onNetworkRequestError(Exception e) {
                showError(e);
            }

            @Override
            public void onCancel() {

            }

        }, true);

        if (SessionTransport.INSTANCE.getAddress() == null) {
            balanceLoaded = true;
        } else {
            Keeper.INSTANCE.getBalance(new GetObjectCallback<BigInteger>() {
                @Override
                public void onRequestResult(BigInteger responce) {
                    balanceLoaded = true;
                    SplashActivity.this.checkContinue();
                }

                @Override
                public void onNetworkRequestError(Exception e) {
                    showError(e);
                }

                @Override
                public void onCancel() {

                }
            }, true);
        }
    }

    private void checkContinue() {
        if (drawsLoaded && balanceLoaded) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void showError(Exception e) {
        if (errorVisible)
            return;

        loadingViewHolder.setRunAnimation(false);
        errorVisible = true;
        final boolean isServerError = e instanceof ServerException;
        try {
            new AlertDialog.Builder(this).setTitle(R.string.error)
                    .setMessage(isServerError ? R.string.requestError : R.string.networkError)
                    .setPositiveButton(R.string.retry, (dialog, which) -> load())
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> finish())
                    .setOnDismissListener(dialog -> errorVisible = false)
                    .show();
        } catch (Exception e1) {
            Log.e(Const.TAG, e1.getMessage(), e1);
        }
    }
}
