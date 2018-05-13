package com.cryptaur.lottery.model;

import java.util.ArrayList;

public class CallbackList<T> extends ArrayList<T> {
    private final CallbackCaller<T> callbackCaller;

    public CallbackList() {
        callbackCaller = null;
    }

    public CallbackList(CallbackCaller<T> callbackCaller) {
        this.callbackCaller = callbackCaller;
    }

    @Override
    public boolean add(T t) {
        return contains(t) ? false : super.add(t);
    }

    public void notifyAllCallbacks() {
        if (callbackCaller != null)
            for (T t : this) {
                callbackCaller.callCallback(t);
            }
    }

    public void notifyAllCallbacks(CallbackCaller<T> callbackCaller) {
        for (T t : this) {
            callbackCaller.callCallback(t);
        }
    }

    public interface CallbackCaller<T> {
        void callCallback(T callback);
    }
}
