package com.cryptaur.lottery.mytickets;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.ITicketStorageRead;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.model.SimpleGetObjectCallback;
import com.cryptaur.lottery.model.TransactionKeeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.TicketsType;
import com.cryptaur.lottery.transport.model.TransactionBuyTicket;
import com.cryptaur.lottery.view.LoadingViewHolder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MyTicketRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final InteractionListener listener;
    private final TicketsType ticketsType;
    private final List<Object> items = new ArrayList<>();
    private final Context context;
    private final RefreshListener refreshListener;
    private final Handler handler = new Handler();
    private BigInteger winAmount;
    private final SimpleGetObjectCallback<Money> getTheWinListener = new SimpleGetObjectCallback<Money>() {
        @Override
        public void onRequestResult(Money responce) {
            winAmount = responce.amount;
            if (winAmount != null && winAmount.equals(BigInteger.ZERO))
                winAmount = null;
            else {
                if (items.size() == 0 || items.get(0) != SimpleValues.Win) {
                    items.add(0, SimpleValues.Win);
                }
            }
            notifyDataSetChanged();
        }
    };

    /**
     * it is true when list with this adapter is primary in paginator (visible to user)
     */
    private boolean isPrimary = false;

    private final GetObjectCallback<ITicketStorageRead> getTicketsListener = new GetObjectCallback<ITicketStorageRead>() {
        @Override
        public void onRequestResult(ITicketStorageRead responce) {
            items.clear();
            if (winAmount != null) {
                items.add(SimpleValues.Win);
            }
            if (ticketsType == TicketsType.Active) {
                items.addAll(TransactionKeeper.INSTANCE.getTicketTransactions());
            }
            items.addAll(responce.getTickets(ticketsType));
            if (responce.canLoadMoreTickets(ticketsType)) {
                items.add(SimpleValues.LoadMore);
            }
            notifyDataSetChanged();
            refreshListener.onRefreshDone();
            if (isPrimary && ticketsType == TicketsType.Played) {
                Keeper.INSTANCE.drawTicketsKeeper.updateLatestShownDrawIdsForPlayedTickets();
                listener.doAction(InteractionListener.Action.InvalidateOptionsMenu, null);
            }
        }

        @Override
        public void onNetworkRequestError(Exception e) {
            refreshListener.onRefreshDone();
        }

        @Override
        public void onCancel() {
            refreshListener.onRefreshDone();
        }
    };

    public MyTicketRecyclerViewAdapter(TicketsType ticketsType, Context context, InteractionListener listener, RefreshListener refreshListener) {
        this.ticketsType = ticketsType;
        this.context = context;
        this.listener = listener;
        this.refreshListener = refreshListener;
        if (SessionTransport.INSTANCE.getAddress() != null) {
            if (ticketsType == TicketsType.Active) {
                items.addAll(TransactionKeeper.INSTANCE.getTicketTransactions());
            }
            ITicketStorageRead ticketStorage = Keeper.INSTANCE.getTicketsStorage();
            items.addAll(ticketStorage.getTickets(ticketsType));
            if (ticketStorage.canLoadMoreTickets(ticketsType))
                items.add(SimpleValues.LoadMore);
            Keeper.INSTANCE.getWinAmount(getTheWinListener, false);
        }
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.view_my_ticket:
                return MyTicketViewHolder.create(parent);

            case R.layout.view_get_the_win:
                return GetTheWinViewHolder.create(parent, listener, refreshListener);

            case R.layout.view_loading:
                return LoadingViewHolder.create(parent);
        }
        throw new RuntimeException("not implemented for view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyTicketViewHolder) {
            Object item = items.get(position);
            if (item instanceof Ticket) {
                ((MyTicketViewHolder) holder).setTicket((Ticket) item);
            } else if (item instanceof TransactionBuyTicket) {
                ((MyTicketViewHolder) holder).setTicketTransaction((TransactionBuyTicket) item);
            }
        } else if (holder instanceof LoadingViewHolder) {
            int amount = items.size() + Const.GET_TICKETS_STEP;
            handler.post(() -> Keeper.INSTANCE.updateTickets(ticketsType, amount, getTicketsListener));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Ticket || item instanceof TransactionBuyTicket) {
            return R.layout.view_my_ticket;
        } else if (item == SimpleValues.Win) {
            return R.layout.view_get_the_win;
        } else
            return R.layout.view_loading;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void fullRefresh() {
        winAmount = null;
        notifyDataSetChanged();
        Keeper.INSTANCE.refreshTickets(true);
        Keeper.INSTANCE.updateTickets(ticketsType, Const.GET_TICKETS_STEP, getTicketsListener);
        Keeper.INSTANCE.getWinAmount(getTheWinListener, true);
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        if (isPrimary && ticketsType == TicketsType.Played) {
            Keeper.INSTANCE.drawTicketsKeeper.updateLatestShownDrawIdsForPlayedTickets();
        }
    }

    private enum SimpleValues {
        Win, LoadMore
    }

    public interface RefreshListener {
        void onRefreshDone();

        void onRefresh();
    }
}
