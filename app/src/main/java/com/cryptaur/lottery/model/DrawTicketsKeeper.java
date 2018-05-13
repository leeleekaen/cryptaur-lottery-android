package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.DrawId;
import com.cryptaur.lottery.transport.model.DrawIds;
import com.cryptaur.lottery.transport.model.Lottery;

public class DrawTicketsKeeper {

    private final ITicketStorageRead ticketsStorage;
    private final TicketsKeeper ticketsKeeper;
    private final Keeper keeper;
    private CurrentDraws currentDraws;
    private final CallbackList<OnLatestDrawsTicketsUpdatedListener> onLatestDrawsTicketsUpdatedListeners
            = new CallbackList<>(c -> c.onLatestDrawsTicketsUpdated(currentDraws));

    DrawTicketsKeeper(Keeper keeper, TicketsKeeper ticketsKeeper) {
        this.keeper = keeper;
        this.ticketsKeeper = ticketsKeeper;
        this.ticketsStorage = ticketsKeeper.getTicketsStorage();

        keeper.currentDrawsKeeper.addListener(this::setCurrentDraws);
        keeper.addTicketsListener(responce -> updateDrawTickets());
    }

    void setCurrentDraws(CurrentDraws currentDraws) {
        this.currentDraws = currentDraws;
        updateDrawTickets();
        /*for (Draw draw : currentDraws.draws) {
            final Lottery lottery = draw.lottery;
            if (draw.number > 1)
                ticketsKeeper.requestTicketStorage(lottery, draw.number - 1, responce -> {
                    if (updateDrawTickets(lottery)){
                        onLatestDrawsTicketsUpdatedListeners.notifyAllCallbacks();
                    }
                });
        }*/
    }

    private void updateDrawTickets() {
        if (currentDraws != null) {
            boolean latestFinishedDrawsUpdated = false;
            boolean loadMoreTickets = false;

            DrawIds latestShownPlayedDraws = DrawTicketsStorage.INSTANCE.getLatestShownDrawIdsForPlayedTickets();
            if (latestShownPlayedDraws == null)
                latestShownPlayedDraws = currentDraws.latestPlayedDraws().prev().prev();

            for (Draw draw : currentDraws.draws) {
                if (draw.number > 1) {
                    DrawId latestShownPlayedDraw = latestShownPlayedDraws.get(draw.lottery);
                    int start = latestShownPlayedDraw == null ? draw.number - 1 : latestShownPlayedDraw.number;
                    for (int drawNumber = start; drawNumber < draw.number; ++drawNumber) {
                        int stored = DrawTicketsStorage.INSTANCE.getWinDrawTickets(draw.lottery, drawNumber);
                        if (stored != -1)
                            continue;

                        int tickets = ticketsStorage.getTicketCountForDraw(draw.lottery, drawNumber);
                        int winTickets = ticketsStorage.getWinTicketCountForDraw(draw.lottery, drawNumber);

                        if (tickets == -1) {
                            loadMoreTickets = true;
                        } else {
                            DrawTicketsStorage.INSTANCE.saveDrawTickets(draw.lottery, drawNumber, tickets, winTickets);
                            latestFinishedDrawsUpdated = true;
                        }
                    }
                }
            }
            if (latestFinishedDrawsUpdated) {
                onLatestDrawsTicketsUpdatedListeners.notifyAllCallbacks();
            }
            if (loadMoreTickets && !ticketsKeeper.isExecutingRequests()) {
                ticketsKeeper.runTicketsUpdate();
                ticketsKeeper.runRequests();
            }
        }
    }

    /*private boolean updateDrawTickets(Lottery lottery) {
        for (Draw draw : currentDraws.draws) {
            if (draw.number > 1) {
                int drawNumber = draw.number - 1;
                int stored = DrawTicketsStorage.INSTANCE.getWinDrawTickets(draw.lottery, drawNumber);
                if (stored != -1)
                    continue;

                int tickets = ticketsStorage.getTicketCountForDraw(draw.lottery, drawNumber);
                int winTickets = ticketsStorage.getWinTicketCountForDraw(draw.lottery, drawNumber);

                if (tickets == -1) {
                    //ticketsKeeper.requestTicketStorage(draw.lottery, drawNumber, null);
                    return false;
                } else {
                    DrawTicketsStorage.INSTANCE.saveDrawTickets(draw.lottery, drawNumber, tickets, winTickets);
                    return true;
                }
            }
        }
        return false;
    }*/

    public int getUnshownTicketsCount() {
        if (currentDraws == null)
            return 0;

        int count = 0;
        DrawIds draws = DrawTicketsStorage.INSTANCE.getLatestShownDrawIdsForPlayedTickets();
        if (draws != null) {
            draws = draws.next();
        }


        for (Lottery lottery : Lottery.values()) {
            Draw currentDraw = currentDraws.getDraw(lottery);
            if (currentDraw == null)
                continue;

            int start = currentDraw.number - 3;

            if (draws != null) {
                DrawId draw = draws.get(lottery);
                if (draw != null)
                    start = draw.number;
            }

            count += DrawTicketsStorage.INSTANCE.getDrawWinTicketsBetween(lottery, start, currentDraw.number);
        }
        return count;
    }

    public void addListener(OnLatestDrawsTicketsUpdatedListener c) {
        onLatestDrawsTicketsUpdatedListeners.add(c);
    }

    public void removeListener(OnLatestDrawsTicketsUpdatedListener c) {
        onLatestDrawsTicketsUpdatedListeners.remove(c);
    }

    public void updateLatestShownDrawIdsForPlayedTickets() {
        DrawIds draws = ticketsStorage.getLatestPlayedTicketDrawIds();
        if (draws.size() > 0) {
            DrawTicketsStorage.INSTANCE.saveLatestShownDrawIdsForPlayedTickets(draws);
        }
    }

    public interface OnLatestDrawsTicketsUpdatedListener {
        void onLatestDrawsTicketsUpdated(CurrentDraws currentDraws);
    }
}
