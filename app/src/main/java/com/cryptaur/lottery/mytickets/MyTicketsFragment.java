package com.cryptaur.lottery.mytickets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.TicketsType;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class MyTicketsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyTicketRecyclerViewAdapter.RefreshListener {

    private static final String ARG_TYCKETS_TYPE = "tickets-type";
    private TicketsType ticketsType = TicketsType.Active;

    private ViewGroup root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MyTicketRecyclerViewAdapter adapter;
    private InteractionListener mListener;
    private boolean isPrimary;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyTicketsFragment() {
    }

    public static MyTicketsFragment newInstance(TicketsType type) {
        MyTicketsFragment fragment = new MyTicketsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYCKETS_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ticketsType = (TicketsType) getArguments().getSerializable(ARG_TYCKETS_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_ticket_list, container, false);

        refreshLayout = root.findViewById(R.id.refresh);
        recyclerView = root.findViewById(R.id.list);

        Context context = root.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyTicketRecyclerViewAdapter(ticketsType, context, mListener, this);
        adapter.setPrimary(isPrimary);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        adapter.fullRefresh();
    }

    @Override
    public void onRefreshDone() {
        refreshLayout.setRefreshing(false);
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        if (adapter != null)
            adapter.setPrimary(isPrimary);
    }
}
