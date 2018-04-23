package com.cryptaur.lottery.model;

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
}
