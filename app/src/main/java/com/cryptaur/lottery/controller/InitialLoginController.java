package com.cryptaur.lottery.controller;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.dialog.EnterPinCodeDialogFragment;
import com.cryptaur.lottery.dialog.FixCloseDialogFragment;
import com.cryptaur.lottery.dialog.LoginDialogFragment;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.exception.ServerException;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;

public class InitialLoginController implements WorkflowController, NetworkRequest.NetworkRequestListener<Session> {

    private final ActivityBase activity;
    private LoginDialogFragment.LoginAction login;
    private EnterPinCodeDialogFragment.OnDonePinInput pin1;
    private EnterPinCodeDialogFragment.OnDonePinInput pin2;

    public InitialLoginController(ActivityBase activity) {
        this.activity = activity;
    }

    @Override
    public void start() {
        LoginDialogFragment.showDialog(activity.getSupportFragmentManager());
    }

    @Override
    public boolean onAction(InteractionListener.IAction action, Fragment fragment) {
        if (action instanceof LoginDialogFragment.LoginAction) {
            this.login = (LoginDialogFragment.LoginAction) action;
            EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.createPinCode, false);
            return true;
        } else if (action instanceof EnterPinCodeDialogFragment.OnDonePinInput) {
            if (pin1 == null) {
                pin1 = (EnterPinCodeDialogFragment.OnDonePinInput) action;
                EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.repeatPinCode, false);
            } else {
                pin2 = (EnterPinCodeDialogFragment.OnDonePinInput) action;
                if (pin1.equals(pin2)) {
                    SessionTransport.INSTANCE.login(activity, new Login(login.login, login.password, pin1.toCharSequence()), this);
                    if (fragment instanceof EnterPinCodeDialogFragment) {
                        ((EnterPinCodeDialogFragment) fragment).showProgress(true);
                    }
                } else {
                    Toast.makeText(activity, "Pins does not match", Toast.LENGTH_SHORT).show();
                    pin1 = null;
                    pin2 = null;
                    EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.repeatPinCode, false);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {

    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, Session responce) {
        Toast.makeText(activity, "Login OK", Toast.LENGTH_SHORT).show();
        FixCloseDialogFragment.closeDialogFragment(activity.getSupportFragmentManager());
        activity.doAction(InteractionListener.Action.Restart, null);
        activity.doAction(InteractionListener.Action.FinishWorkflow, null);
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        pin1 = pin2 = null;
        LoginDialogFragment.showDialog(activity.getSupportFragmentManager(), login);

        final boolean isServerError = e instanceof ServerException;
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                new AlertDialog.Builder(activity).setTitle(R.string.error)
                        .setMessage(isServerError ? R.string.errorLoggingIn : R.string.networkError)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .show(), 200);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
    }
}
