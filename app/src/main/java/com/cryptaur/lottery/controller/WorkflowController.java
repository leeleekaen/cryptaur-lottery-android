package com.cryptaur.lottery.controller;

import android.support.v4.app.Fragment;

import com.cryptaur.lottery.InteractionListener;

public interface WorkflowController {
    void start();

    /**
     * @param action
     * @param fragment
     * @return true if action consumed
     */
    boolean onAction(InteractionListener.IAction action, Fragment fragment);
}
