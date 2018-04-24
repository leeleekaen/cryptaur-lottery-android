package com.cryptaur.lottery.buytickets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.util.FilteredDividerItemDecoration;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link InteractionListener}
 * interface.
 */
public class DrawsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DrawsRecyclerViewAdapter.RefreshListener {

    private static final String ARG_LOTTERY = "lottery";
    private Lottery lottery;
    private InteractionListener mListener;
    private ViewGroup root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private DrawsRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrawsFragment() {
    }

    public static DrawsFragment newInstance(Lottery lottery) {
        DrawsFragment fragment = new DrawsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOTTERY, lottery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lottery = (Lottery) getArguments().getSerializable(ARG_LOTTERY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_draw_list, container, false);

        refreshLayout = root.findViewById(R.id.refresh);
        recyclerView = root.findViewById(R.id.list);

        Context context = root.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new DrawsRecyclerViewAdapter(lottery, mListener, this);
        recyclerView.setAdapter(adapter);
        FilteredDividerItemDecoration dividerItemDecoration = new FilteredDividerItemDecoration(root.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.h_spacer));
        recyclerView.addItemDecoration(dividerItemDecoration);

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
        mListener = null;
    }

    @Override
    public void onRefresh() {
        adapter.refresh();
    }

    @Override
    public void onRefreshDone() {
        refreshLayout.setRefreshing(false);
    }
}
