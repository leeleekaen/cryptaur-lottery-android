package com.cryptaur.lottery.model;

import android.support.annotation.Nullable;

import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.LotteryTicketsList;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsToLoad;

import java.util.ArrayList;
import java.util.List;

public class LotteryTicketsQueueJoiner {
    private final List<LotteryTicketsQueue> queues = new ArrayList<>();

    public LotteryTicketsQueueJoiner(Lottery[] lotteries) {
        for (int i = 0; i < lotteries.length; i++) {
            queues.add(new LotteryTicketsQueue(lotteries[i]));
        }
    }

    /**
     * adds tickets reply to corresponding tickets queue
     *
     * @param ticketsList
     */
    public void add(LotteryTicketsList ticketsList) {
        Lottery lottery = ticketsList.lottery;
        for (LotteryTicketsQueue queue : queues) {
            if (queue.lottery == lottery) {
                queue.add(ticketsList);
                return;
            }
        }
    }

    /**
     * @return next ticket;
     * return null if there is no tickets left or if we should load more tickets for some queue
     */
    @Nullable
    public Ticket next() {
        Ticket result = null;

        while (result == null) {
            if (queues.size() == 0)
                return null;

            result = queues.get(0).peek();
            if (result == null) {
                if (queues.get(0).canLoadMore())
                    return null;
                else {
                    queues.remove(0);
                }
            }
        }

        if (queues.size() == 1)
            return queues.get(0).poll();

        int bestQueue = 0;
        for (int i = 1; i < queues.size(); i++) {
            LotteryTicketsQueue queue = queues.get(i);

            Ticket pretendent = queue.peek();
            if (pretendent == null) {
                if (queues.get(i).canLoadMore())
                    return null;
                else {
                    queues.remove(i);
                    --i;
                }
                continue;
            }
            if (result.drawDate.isBefore(pretendent.drawDate)) {
                result = pretendent;
                bestQueue = i;
            }
        }
        return queues.get(bestQueue).poll();
    }

    /**
     * @return list of requests to load;
     * empty list if we don't need to load right now
     * null if there is nothing to load
     */
    public List<TicketsToLoad> getTicketsToLoad() {
        if (queues.size() == 0)
            return null;
        List<TicketsToLoad> result = new ArrayList<>();
        for (LotteryTicketsQueue queue : queues) {
            if (queue.shouldLoad()) {
                result.add(queue.nextPaginatorPageRequest());
            }
        }
        return result;
    }

    public boolean canLoadMoreTickets() {
        return queues.size() > 0;
    }

    /**
     * resets this object to initial state;
     *
     * @param lotteries -- lotteries that should be loaded/presented
     */
    public void reset(Lottery[] lotteries) {
        queues.clear();
        for (int i = 0; i < lotteries.length; i++) {
            queues.add(new LotteryTicketsQueue(lotteries[i]));
        }
    }

    public boolean canLoadMoreTickets(Lottery lottery) {
        for (LotteryTicketsQueue queue : queues) {
            if (queue.lottery == lottery)
                return true;
        }
        return false;
    }
}
