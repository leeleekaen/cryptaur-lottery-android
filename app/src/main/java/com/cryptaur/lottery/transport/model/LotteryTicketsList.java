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
    public final int smallestIndex;
    public final int largestIndex;

    public LotteryTicketsList(Lottery lottery, int startFrom, JSONObject source) throws JSONException {
        this.lottery = lottery;
        this.startFrom = startFrom;
        JSONArray arr = source.getJSONArray("tickets");
        tickets = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); ++i) {
            tickets.add(new Ticket(lottery, arr.getJSONObject(i)));
        }
        if (tickets.size() > 0) {
            largestIndex = tickets.get(0).index;
            smallestIndex = tickets.get(tickets.size() - 1).index;
        } else {
            smallestIndex = largestIndex = 0;
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
