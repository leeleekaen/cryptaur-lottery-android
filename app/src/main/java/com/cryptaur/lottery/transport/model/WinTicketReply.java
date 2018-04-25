package com.cryptaur.lottery.transport.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinTicketReply {
    @NonNull
    public final List<WinTicket> tickets;

    @NonNull
    public final WinTicketsRequest request;

    public WinTicketReply(WinTicketsRequest request, JSONObject source) throws JSONException {
        this.request = request;

        JSONArray arr = source.getJSONArray("tickets");
        List<WinTicket> tickets = new ArrayList<>(arr.length());
        for (int i = 0; i < arr.length(); ++i) {
            tickets.add(new WinTicket(request.draw, arr.getJSONObject(i)));
        }
        this.tickets = Collections.unmodifiableList(tickets);
    }
}