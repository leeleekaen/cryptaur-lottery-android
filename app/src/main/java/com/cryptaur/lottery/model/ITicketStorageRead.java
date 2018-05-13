package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.DrawIds;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsType;

import java.util.Collection;

/**
 * intarface for reading available tickets;
 */
public interface ITicketStorageRead {
    /**
     * returns tickets
     *
     * @param type -- played and not played
     * @return
     */
    Collection<Ticket> getTickets(TicketsType type);

    /**
     * return true if we might get more tickets of this type from server
     *
     * @param type
     * @return
     */
    boolean canLoadMoreTickets(TicketsType type);

    /**
     * return user's ticket count for a draw.
     * return -1 if still have not enough data for that draw
     *
     * @param lottery
     * @param drawIndex
     * @return
     */
    int getTicketCountForDraw(Lottery lottery, int drawIndex);

    int getWinTicketCountForDraw(Lottery lottery, int drawIndex);

    /**
     * @return draw ids for latest played tickets available
     */
    DrawIds getLatestPlayedTicketDrawIds();
}
