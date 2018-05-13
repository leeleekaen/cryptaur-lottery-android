package com.cryptaur.lottery.model;

import android.support.annotation.Nullable;

import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.model.DrawId;
import com.cryptaur.lottery.transport.model.DrawIds;
import com.cryptaur.lottery.transport.model.Lottery;

import java.util.HashMap;
import java.util.Locale;

import io.paperdb.Paper;

public class DrawTicketsStorage {
    public static final DrawTicketsStorage INSTANCE = new DrawTicketsStorage();

    private static final String BOOK = "drawTickets";

    private static final String KEY_LATEST_SHOWN_DRAW_IDS_FOR_PLAYED_TICKETS = "LatestShownDrawIdsForPlayedTickets";

    private final HashMap<String, int[]> cashedDrawTicketCounts = new HashMap<>();
    private DrawIds latestShownDrawIdsForPlayedTickets;

    private String book;

    private String keyLatestShownDrawIdsForPlayedTickets;

    public DrawTicketsStorage() {
        SessionTransport.INSTANCE.addOnAddressChangedListener(addr -> {
            book = addr == null ? null : BOOK + addr;
            keyLatestShownDrawIdsForPlayedTickets = addr == null ? null : KEY_LATEST_SHOWN_DRAW_IDS_FOR_PLAYED_TICKETS + addr;
            cashedDrawTicketCounts.clear();
            latestShownDrawIdsForPlayedTickets = null;
        });
    }

    public void saveDrawTickets(Lottery lottery, int drawIndex, int tickets, int winTickets) {
        if (book == null)
            return;

        String key = lotteryCountKey(lottery, drawIndex);
        int stored[] = getDrawTickets(key);

        if (stored == null || stored[0] != tickets || stored[1] != winTickets) {
            Paper.book(book).write(key, new int[]{tickets, winTickets});
        }
    }

    public int getDrawTickets(Lottery lottery, int drawIndex) {
        int[] result = getDrawTickets(lotteryCountKey(lottery, drawIndex));
        return result == null ? -1 : result[0];
    }

    public int getWinDrawTickets(Lottery lottery, int drawIndex) {
        int[] result = getDrawTickets(lotteryCountKey(lottery, drawIndex));
        return result == null ? -1 : result[1];
    }

    private int[] getDrawTickets(String key) {
        if (book == null)
            return null;
        if (cashedDrawTicketCounts.containsKey(key))
            return cashedDrawTicketCounts.get(key);
        int[] value = Paper.book(book).read(key, null);

        if (value != null) {
            cashedDrawTicketCounts.put(key, value);
        }
        return value;
    }

    private String lotteryCountKey(Lottery lottery, int drawIndex) {
        return String.format(Locale.US, "count_%s_%d", lottery.displayName(), drawIndex);
    }

    @Nullable
    public DrawIds getLatestShownDrawIdsForPlayedTickets() {
        if (keyLatestShownDrawIdsForPlayedTickets == null)
            return null;
        if (latestShownDrawIdsForPlayedTickets != null)
            return latestShownDrawIdsForPlayedTickets;
        return latestShownDrawIdsForPlayedTickets = Paper.book().read(KEY_LATEST_SHOWN_DRAW_IDS_FOR_PLAYED_TICKETS);
    }

    public void saveLatestShownDrawIdsForPlayedTickets(DrawIds ids) {
        if (keyLatestShownDrawIdsForPlayedTickets == null)
            return;
        DrawIds stored = getLatestShownDrawIdsForPlayedTickets();

        if (stored != null) {
            for (int i = 0; i < stored.size(); i++) {
                DrawId oldDrawId = stored.get(i);
                DrawId drawId = ids.get(oldDrawId.lottery);
                if (drawId == null) {
                    ids.add(oldDrawId);
                } else if (drawId.number < oldDrawId.number) {
                    ids.remove(drawId);
                    ids.add(oldDrawId);
                }
            }
        }
        ids.sortByLotteryId();

        if (!ids.equals(stored)) {
            Paper.book().write(KEY_LATEST_SHOWN_DRAW_IDS_FOR_PLAYED_TICKETS, ids);
            latestShownDrawIdsForPlayedTickets = ids;
        }
    }

    public int getDrawWinTicketsBetween(Lottery lottery, int start, int end) {
        int result = 0;
        for (int i = start; i < end; ++i) {
            int amount = getWinDrawTickets(lottery, i);
            if (amount > 0)
                result += amount;
        }
        return result;
    }
}
