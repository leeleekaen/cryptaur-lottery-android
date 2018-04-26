package com.cryptaur.lottery.transport.request;

import com.cryptaur.lottery.transport.base.RequestType;
import com.cryptaur.lottery.transport.model.Session;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.model.TransactionBuyTicket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class BuyTicketRequest extends BaseLotteryRequest<Transaction> {

    private static final String METHOD = "api/buyTickets";

    private final Ticket ticket;

    public BuyTicketRequest(OkHttpClient client, Ticket ticket, Session session, NetworkRequestListener<Transaction> listener) {
        super(client, listener);
        this.ticket = ticket;
        setAuthString(session.key);
    }

    @Override
    protected void execRequest() throws JSONException {
        JSONObject requestObj = new JSONObject();
        //requestObj.put("authKey", session.key);
        requestObj.put("lotteryId", ticket.lottery.getServerId());
        requestObj.put("drawIndex", ticket.drawIndex);
        JSONArray numbersArr = new JSONArray();
        for (int i = 0; i < ticket.numbers.length; i++) {
            numbersArr.put(ticket.numbers[i]);
        }
        requestObj.put("numbers", numbersArr);
        execSimpleRequest(METHOD, RequestType.Post, requestObj.toString());
    }

    @Override
    protected Transaction parseJson(JSONObject source) throws JSONException {
        String hash = source.getString("trxHash");
        return new TransactionBuyTicket(hash, ticket);
    }
}
