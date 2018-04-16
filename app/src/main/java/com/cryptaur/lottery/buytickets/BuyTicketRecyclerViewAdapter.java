package com.cryptaur.lottery.buytickets;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Lottery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuyTicketRecyclerViewAdapter extends RecyclerView.Adapter<BuyTicketRecyclerViewAdapter.NumberViewHolder> {

    private final Lottery lottery;
    private final NumbersListener mListener;
    private final List<Integer> checkedNumbers = new ArrayList<>(6);

    public BuyTicketRecyclerViewAdapter(Lottery lottery, NumbersListener listener) {
        this.lottery = lottery;
        mListener = listener;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckedTextView view = (CheckedTextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_ticket_number, parent, false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NumberViewHolder holder, int position) {
        holder.setNumber(position + 1);
    }

    @Override
    public int getItemCount() {
        return lottery.getMaxValue();
    }

    public List<Integer> getCheckedNumbers() {
        return checkedNumbers;
    }

    public void clear() {
        checkedNumbers.clear();
        notifyDataSetChanged();
    }

    public interface NumbersListener {
        void onNumbersChanged(List<Integer> numbers, boolean filled);
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final CheckedTextView mView;
        Integer number;

        public NumberViewHolder(CheckedTextView view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
        }

        void setNumber(int number) {
            this.number = number;
            String text = String.format(Locale.getDefault(), "%d", number);
            mView.setText(text);
            if (checkedNumbers.contains(this.number))
                mView.setChecked(true);
        }

        @Override
        public void onClick(View v) {
            if (mView.isChecked()) {
                mView.setChecked(false);
                checkedNumbers.remove(number);
            } else if (checkedNumbers.size() < lottery.getNumbersAmount()) {
                mView.setChecked(true);
                checkedNumbers.add(number);
            }
            mListener.onNumbersChanged(checkedNumbers, checkedNumbers.size() == lottery.getNumbersAmount());
        }
    }
}
