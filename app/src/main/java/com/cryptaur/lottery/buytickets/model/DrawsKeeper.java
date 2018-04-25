package com.cryptaur.lottery.buytickets.model;

import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.DrawsReply;
import com.cryptaur.lottery.transport.model.DrawsRequest;
import com.cryptaur.lottery.transport.model.Lottery;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class DrawsKeeper {
    private final TreeSet<Draw> draws = new TreeSet<>(new DrawsComparator());

    private final Lottery lottery;

    private boolean canLoadMore = true;

    public DrawsKeeper(Lottery lottery) {
        this.lottery = lottery;
    }

    public void add(DrawsReply draws) {
        this.draws.addAll(draws.draws);
        if (draws.draws == null || draws.draws.size() == 0 || this.draws.last().number == 1)
            canLoadMore = false;
        if (this.draws.size() > 0 && !this.draws.first().isPlayed()) {
            this.draws.remove(this.draws.first());
        }
    }

    public Collection<Draw> getDraws() {
        return Collections.unmodifiableCollection(draws);
    }

    public DrawsRequest nextRequest() {
        return new DrawsRequest(lottery, draws.size(), 20);
    }

    public boolean canLoadMore() {
        return canLoadMore;
    }

    public void reset() {
        draws.clear();
        canLoadMore = true;
    }

    private static class DrawsComparator implements Comparator<Draw> {
        @Override
        public int compare(Draw o1, Draw o2) {
            return o2.number - o1.number;
        }
    }
}
