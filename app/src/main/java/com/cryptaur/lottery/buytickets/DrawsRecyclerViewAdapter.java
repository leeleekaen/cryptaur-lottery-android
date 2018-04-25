package com.cryptaur.lottery.buytickets;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.buytickets.model.DrawsKeeper;
import com.cryptaur.lottery.model.SingleRequest;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.DrawsReply;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.FilteredDividerItemDecoration;
import com.cryptaur.lottery.util.SimpleViews;
import com.cryptaur.lottery.view.LoadingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DrawsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements FilteredDividerItemDecoration.IDecorationFilter {

    private final DrawsKeeper drawsKeeper;
    private final List<Object> items = new ArrayList<>();

    private final InteractionListener mListener;

    private final SingleRequest<DrawsReply> requestExecutor;

    public DrawsRecyclerViewAdapter(Lottery lottery, InteractionListener listener, RefreshListener refreshListener) {
        this.drawsKeeper = new DrawsKeeper(lottery);
        items.add(SimpleViews.LoadMore);
        mListener = listener;
        requestExecutor = new SingleRequest<>(l -> Transport.INSTANCE.getDraws(drawsKeeper.nextRequest(), l));
        requestExecutor.setRequestAbortListener(e -> refreshListener.onRefreshDone());
        requestExecutor.setRequestDoneListener(result -> {
            drawsKeeper.add(result);
            items.clear();
            items.addAll(drawsKeeper.getDraws());
            if (drawsKeeper.canLoadMore())
                items.add(SimpleViews.LoadMore);
            notifyDataSetChanged();
            refreshListener.onRefreshDone();
        });
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_draw_in_list:
                return new DrawInListViewHolder((ViewGroup) view, mListener);
            case R.layout.view_loading:
                return new LoadingViewHolder(view);
        }

        throw new RuntimeException("NOt implemented for: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            requestExecutor.executeRequest();
        } else if (holder instanceof DrawInListViewHolder) {
            ((DrawInListViewHolder) holder).setDraw((Draw) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Draw)
            return R.layout.view_draw_in_list;
        return R.layout.view_loading;
    }

    public void refresh() {
        drawsKeeper.reset();
        requestExecutor.executeRequest();
    }

    @Override
    public boolean shouldDrawDecorator(int position) {
        return items.get(position) instanceof Draw;
    }

    public interface RefreshListener {
        void onRefreshDone();
    }
}
