package com.cryptaur.lottery;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public interface InteractionListener {
    void doAction(IAction action, @Nullable Fragment fragment);

    enum Action implements IAction {
        GetTheWin, CloseThisFragment, ForgotPasswordAction, UseLoginAndPassword, FinishWorkflow, RefreshWallet, Login
    }

    interface IAction {
    }
}
