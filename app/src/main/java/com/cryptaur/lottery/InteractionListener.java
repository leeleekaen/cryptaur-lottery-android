package com.cryptaur.lottery;

public interface InteractionListener {
    void onDoAction(Action action);

    enum Action{
        GetTheWin,
    }
}
