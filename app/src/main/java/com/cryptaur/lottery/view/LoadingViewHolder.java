package com.cryptaur.lottery.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptaur.lottery.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public LoadingViewHolder(View itemView) {
        super(itemView);
    }

    public static LoadingViewHolder create(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_loading, parent, false);
        return new LoadingViewHolder(view);
    }
}
