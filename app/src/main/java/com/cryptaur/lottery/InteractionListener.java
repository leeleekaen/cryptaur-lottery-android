package com.cryptaur.lottery;


import android.support.v4.app.Fragment;

public interface InteractionListener {
    void doAction(IAction action, Fragment fragment);

    enum Action implements IAction {
        GetTheWin, CloseThisFragment, ForgotPasswordAction, UseLoginAndPassword, FinishWorkflow, RefreshWallet
    }

    interface IAction {
    }
}
