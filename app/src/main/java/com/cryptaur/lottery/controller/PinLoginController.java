package com.cryptaur.lottery.controller;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.dialog.EnterPinCodeDialogFragment;
import com.cryptaur.lottery.dialog.EnterPinCodeDialogFragment.OnDonePinInput;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

public class PinLoginController implements WorkflowController, NetworkRequest.NetworkRequestListener<Session> {

    private final ActivityBase activity;

    public PinLoginController(ActivityBase activity) {
        this.activity = activity;
    }

    @Override
    public void start() {
        EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.enter_your_pin_code_to_login, true);
    }

    @Override
    public boolean onAction(InteractionListener.IAction action, Fragment fragment) {
        if (action instanceof OnDonePinInput) {
            SessionTransport.INSTANCE.login(activity, ((OnDonePinInput) action).toCharSequence().toString(), this);
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
        Toast.makeText(activity, "Error logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        Fragment fragment = FixCloseDialogFragment.findDialogFragment(activity.getSupportFragmentManager());
        if (fragment instanceof EnterPinCodeDialogFragment) {
            ((EnterPinCodeDialogFragment) fragment).resetPinInput();
        } else
            EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.enter_your_pin_code_to_login, true);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
        Fragment fragment = FixCloseDialogFragment.findDialogFragment(activity.getSupportFragmentManager());
        if (fragment instanceof EnterPinCodeDialogFragment) {
            ((EnterPinCodeDialogFragment) fragment).resetPinInput();
        } else
            EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.enter_your_pin_code_to_login, true);
    }
}
