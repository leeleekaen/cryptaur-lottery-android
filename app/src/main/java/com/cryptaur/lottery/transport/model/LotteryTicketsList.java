package com.cryptaur.lottery.transport.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LotteryTicketsList {
    public final List<Ticket> tickets;
    public final int startFrom;
    public final Lottery lottery;

    public LotteryTicketsList(Lottery lottery, int startFrom, List<Ticket> tickets) {
        this.tickets = tickets;
        this.startFrom = startFrom;
        this.lottery = lottery;
    }

    public LotteryTicketsList(Lottery lottery, int startFrom, JSONObject source) throws JSONException {
        this.lottery = lottery;
        this.startFrom = startFrom;
        JSONArray arr = source.getJSONArray("tickets");
        tickets = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); ++i) {
            tickets.add(new Ticket(lottery, arr.getJSONObject(i)));
        }
    }

    /*
    {
    "tickets":[
,
        .....
    ]
}
     */
}
