package com.cryptaur.lottery.login;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cryptaur.lottery.ActivityBase;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.controller.WorkflowController;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.Login;
import com.cryptaur.lottery.util.FixCloseDialogFragment;

public class InitialLoginController implements WorkflowController, NetworkRequest.NetworkRequestListener {

    private final ActivityBase activity;
    LoginFragment.LoginAction login;
    EnterPinCodeDialogFragment.OnDonePinUnput pin1;
    EnterPinCodeDialogFragment.OnDonePinUnput pin2;

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
        } else if (action instanceof EnterPinCodeDialogFragment.OnDonePinUnput) {
            if (pin1 == null) {
                pin1 = (EnterPinCodeDialogFragment.OnDonePinUnput) action;
                EnterPinCodeDialogFragment.showDialog(activity.getSupportFragmentManager(), R.string.repeatPinCode, false);
            } else {
                pin2 = (EnterPinCodeDialogFragment.OnDonePinUnput) action;
                if (pin1.equals(pin2)) {
                    Transport.INSTANCE.login(activity, new Login(login.login, login.password, pin1.toCharSequence()), this);
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
    public void onNetworkRequestDone(NetworkRequest request, Object responce) {
        Toast.makeText(activity, "Login OK", Toast.LENGTH_SHORT).show();
        FixCloseDialogFragment.closeDialogFragment(activity.getSupportFragmentManager());
        activity.doAction(InteractionListener.Action.RefreshWallet, null);
        activity.doAction(InteractionListener.Action.FinishWorkflow, null);
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        Toast.makeText(activity, "Error logging in", Toast.LENGTH_SHORT).show();
        pin1 = pin2 = null;
        LoginFragment.showDialog(activity.getSupportFragmentManager(), login);
    }

    @Override
    public void onCancel(NetworkRequest request) {
        Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show();
    }
}
