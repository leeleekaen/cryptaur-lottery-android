package com.cryptaur.lottery.mytickets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.ITicketStorageRead;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsType;
import com.cryptaur.lottery.view.LoadingViewHolder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MyTicketRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final InteractionListener listener;
    private final TicketsType ticketsType;
    private final List<Ticket> ticketList = new ArrayList<>();
    private final Context context;
    private BigInteger winAmount;
    private boolean canLoadMoreTickets;
    private int getTheWinPosition = -1;
    private int loadMorePosition = -1;
    private int itemAmount = 0;
    private final GetObjectCallback<BigInteger> getTheWinListener = new GetObjectCallback<BigInteger>() {
        @Override
        public void onRequestResult(BigInteger responce) {
            winAmount = responce;
            refreshPositions();
            notifyDataSetChanged();
        }

        @Override
        public void onNetworkRequestError(Exception e) {

        }

        @Override
        public void onCancel() {

        }
    };
    private final GetObjectCallback<ITicketStorageRead> getTicketsListener = new GetObjectCallback<ITicketStorageRead>() {
        @Override
        public void onRequestResult(ITicketStorageRead responce) {
            ticketList.clear();
            ticketList.addAll(responce.getTickets(ticketsType));
            canLoadMoreTickets = responce.canLoadMoreTickets(ticketsType);
            refreshPositions();
            notifyDataSetChanged();
        }

        @Override
        public void onNetworkRequestError(Exception e) {

        }

        @Override
        public void onCancel() {

        }
    };

    public MyTicketRecyclerViewAdapter(TicketsType ticketsType, Context context, InteractionListener listener) {
        this.ticketsType = ticketsType;
        this.context = context;
        this.listener = listener;
        ITicketStorageRead ticketStorage = Keeper.getInstance(context).getTicketsStorage();
        ticketList.addAll(ticketStorage.getTickets(ticketsType));
        canLoadMoreTickets = ticketStorage.canLoadMoreTickets(ticketsType);
        /*if (ticketStorage.canLoadMoreTickets(ticketsType)){
            Keeper.getInstance(context).updateTickets(getTicketsListener);
        }*/

        refreshPositions();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.view_my_ticket:
                return MyTicketViewHolder.create(parent);

            case R.layout.view_get_the_win:
                return GetTheWinViewHolder.create(parent, listener);

            case R.layout.view_loading:
                return LoadingViewHolder.create(parent);
        }
        throw new RuntimeException("not implemented for view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyTicketViewHolder) {
            boolean win = winAmount != null && winAmount.compareTo(BigInteger.ZERO) > 0;
            ((MyTicketViewHolder) holder).setTicket(ticketList.get(position - (win ? 1 : 0)));
        } else if (holder instanceof LoadingViewHolder) {
            Keeper.getInstance(context).updateTickets(getTicketsListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getTheWinPosition)
            return R.layout.view_get_the_win;
        if (position == loadMorePosition)
            return R.layout.view_loading;
        return R.layout.view_my_ticket;
    }

    @Override
    public int getItemCount() {
        return itemAmount;
    }

    private void refreshPositions() {
        boolean win = winAmount != null && winAmount.compareTo(BigInteger.ZERO) > 0;
        getTheWinPosition = win ? 0 : -1;
        itemAmount = ticketList.size() + (win ? 1 : 0) + (canLoadMoreTickets ? 1 : 0);
        loadMorePosition = canLoadMoreTickets ? itemAmount - 1 : -1;
    }

    public void fullRefresh() {
        winAmount = null;
        ticketList.clear();
        refreshPositions();
        notifyDataSetChanged();
        Keeper.getInstance(context).refreshTickets();
        Keeper.getInstance(context).updateTickets(getTicketsListener);
        Keeper.getInstance(context).getUnusedWin(getTheWinListener);
    }
}
