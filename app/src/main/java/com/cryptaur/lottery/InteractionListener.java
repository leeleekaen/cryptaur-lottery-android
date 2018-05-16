package com.cryptaur.lottery;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public interface InteractionListener {
    void doAction(IAction action, @Nullable Fragment fragment);

    enum Action implements IAction {
        CloseThisFragment, ForgotPasswordAction, UseLoginAndPassword, FinishWorkflow, Restart,
        Login, Logout, MyTickets, InvalidateOptionsMenu, HowToPlay,
        ShowProgress, HideProgress
    }

    interface IAction {
    }
}
