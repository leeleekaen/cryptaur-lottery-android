package com.cryptaur.lottery.buytickets;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.util.Strings;

import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

public class DrawInListViewHolder extends RecyclerView.ViewHolder {
    private static final DateTimeFormatter dateTimeformat = DateTimeFormatter.ofPattern("dd MMM, hh:mm:ss");
    private final ViewGroup view;
    private final TextView[] ticketNumbers = new TextView[6];
    private final TextView drawDate;
    private final TextView jackpotView;
    private final TextView drawNumber;
    private Draw draw;

    public DrawInListViewHolder(ViewGroup view, InteractionListener listener) {
        super(view);
        this.view = view;
        drawDate = view.findViewById(R.id.drawDate);
        drawNumber = view.findViewById(R.id.drawNumber);
        jackpotView = view.findViewById(R.id.jackpot);
        ticketNumbers[0] = view.findViewById(R.id.ticketNumber1);
        ticketNumbers[1] = view.findViewById(R.id.ticketNumber2);
        ticketNumbers[2] = view.findViewById(R.id.ticketNumber3);
        ticketNumbers[3] = view.findViewById(R.id.ticketNumber4);
        ticketNumbers[4] = view.findViewById(R.id.ticketNumber5);
        ticketNumbers[5] = view.findViewById(R.id.ticketNumber6);
        view.setOnClickListener(v -> listener.doAction(new ShowDrawDetailsAction(draw), null));
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
        drawNumber.setText(view.getResources().getString(R.string.draw_number, draw.number));

        for (int i = 0; i < draw.numbers.length; i++) {
            TextView view = ticketNumbers[i];
            view.setVisibility(View.VISIBLE);
            int number = draw.numbers[i];
            String numberStr = String.format(Locale.getDefault(), "%d", number);
            view.setText(numberStr);
        }
        for (int i = draw.numbers.length; i < 6; i++) {
            ticketNumbers[i].setVisibility(View.GONE);
        }

        String time = dateTimeformat.format(draw.startTime.atZone(ZoneId.systemDefault()));
        drawDate.setText(time);
        drawDate.setVisibility(View.VISIBLE);

        SpannableStringBuilder bld = new SpannableStringBuilder();
        bld.append(view.getResources().getString(R.string.jackpot)).append(" ");
        int start = bld.length();
        bld.append(Strings.toDecimalString(draw.jackpot, 8, 0, ".", ","));
        bld.append(view.getResources().getString(R.string.cpt));

        Object span = new StyleSpan(Typeface.BOLD);
        bld.setSpan(span, start, bld.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span = new ForegroundColorSpan(0xFF634285);
        bld.setSpan(span, start, bld.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        jackpotView.setText(bld);
    }
}
