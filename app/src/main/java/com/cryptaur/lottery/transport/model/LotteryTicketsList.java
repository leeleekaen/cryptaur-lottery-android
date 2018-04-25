package com.cryptaur.lottery.transport.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryTicketsList {
    /**
     * lottery tickets belong to
     */
    public final Lottery lottery;

    /**
     * tickets, returned by server
     */
    public final List<Ticket> tickets;

    /**
     * paginator request start
     */
    public final int startFrom;


    public LotteryTicketsList(Lottery lottery, int startFrom, JSONObject source) throws JSONException {
        this.lottery = lottery;
        this.startFrom = startFrom;
        JSONArray arr = source.getJSONArray("tickets");
        List<Ticket> tickets = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); ++i) {
            tickets.add(new Ticket(lottery, arr.getJSONObject(i)));
        }
        this.tickets = Collections.unmodifiableList(tickets);
    }
}
