package com.cryptaur.lottery.buytickets;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.transport.model.Draw;

public class ShowDrawDetailsAction implements InteractionListener.IAction {
    public final Draw draw;

    public ShowDrawDetailsAction(Draw draw) {
        this.draw = draw;
    }
}
