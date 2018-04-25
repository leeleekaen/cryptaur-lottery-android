package com.cryptaur.lottery.buytickets;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.buytickets.model.WinTicketsKeeper;
import com.cryptaur.lottery.model.SingleRequest;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.WinTicket;
import com.cryptaur.lottery.transport.model.WinTicketReply;
import com.cryptaur.lottery.util.FilteredDividerItemDecoration;
import com.cryptaur.lottery.util.SimpleViews;
import com.cryptaur.lottery.view.LoadingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MyDrawDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements FilteredDividerItemDecoration.IDecorationFilter {

    private final List<Object> items = new ArrayList<>();
    private final WinTicketsKeeper ticketsKeeper;
    private final SingleRequest<WinTicketReply> requestExecutor;

    public MyDrawDetailsRecyclerViewAdapter(Draw draw) {
        ticketsKeeper = new WinTicketsKeeper(draw);
        items.add(draw);
        items.add(SimpleViews.LoadMore);
        requestExecutor = new SingleRequest<>(l -> Transport.INSTANCE.getWinTickets(ticketsKeeper.nextRequest(), l));
        requestExecutor.setRequestDoneListener(result -> {
            ticketsKeeper.add(result);
            items.clear();
            items.add(draw);
            items.addAll(ticketsKeeper.getTickets());
            if (ticketsKeeper.canLoadMore()) {
                items.add(SimpleViews.LoadMore);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_loading:
                return new LoadingViewHolder(view);

            case R.layout.view_draw_details:
                return new DrawDetailsViewHolder((ViewGroup) view);

            case R.layout.view_win_ticket:
                return new WinTicketViewHolder((ViewGroup) view);
        }
        throw new RuntimeException("Not implemented for " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WinTicketViewHolder) {
            ((WinTicketViewHolder) holder).setTicket((WinTicket) items.get(position));
        } else if (holder instanceof DrawDetailsViewHolder) {
            ((DrawDetailsViewHolder) holder).setDraw((Draw) items.get(position));
        } else if (holder instanceof LoadingViewHolder) {
            requestExecutor.executeRequest();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof WinTicket) {
            return R.layout.view_win_ticket;
        } else if (item instanceof Draw) {
            return R.layout.view_draw_details;
        } else if (item == SimpleViews.LoadMore) {
            return R.layout.view_loading;
        }
        throw new RuntimeException("not implemented for: " + item.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean shouldDrawDecorator(int position) {
        return items.get(position) instanceof WinTicket;
    }
}
