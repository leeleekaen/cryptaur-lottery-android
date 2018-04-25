package com.cryptaur.lottery.buytickets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.util.FilteredDividerItemDecoration;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link InteractionListener}
 * interface.
 */
public class DrawDetailsFragment extends Fragment {

    private static final String ARG_DRAW = "draw";

    private Draw draw;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrawDetailsFragment() {
    }

    public static DrawDetailsFragment newInstance(Draw draw) {
        DrawDetailsFragment fragment = new DrawDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DRAW, draw);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            draw = (Draw) getArguments().getSerializable(ARG_DRAW);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw_details, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyDrawDetailsRecyclerViewAdapter(draw));
            FilteredDividerItemDecoration dividerItemDecoration = new FilteredDividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.h_spacer_pale_lavander));
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        return view;
    }
}
