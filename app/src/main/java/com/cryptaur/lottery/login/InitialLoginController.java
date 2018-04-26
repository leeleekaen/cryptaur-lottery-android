package com.cryptaur.lottery.login;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.controller.WorkflowController;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

public class InitialLoginController implements WorkflowController, NetworkRequest.NetworkRequestListener<Session> {

    private final ActivityBase activity;
    private LoginFragment.LoginAction login;
    private EnterPinCodeDialogFragment.OnDonePinInput pin1;
    private EnterPinCodeDialogFragment.OnDonePinInput pin2;

    public InitialLoginController(ActivityBase activity) {
        this.activity = activity;
    }

    @Override
    public void start() {
        LoginFragment.showDialog(activity.getSupportFragmentManager());
    }

    @Override
    public boolean onAction(InteractionListener.IAction action, Fragment fragment) {
        if (action instanceof LoginFragment.LoginAction) {
            this.login = (LoginFragment.LoginAction) action;
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
        Toast.makeText(activity, "Error logging in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        pin1 = pin2 = null;
        LoginFragment.showDialog(activity.getSupportFragmentManager(), login);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
    }
}
