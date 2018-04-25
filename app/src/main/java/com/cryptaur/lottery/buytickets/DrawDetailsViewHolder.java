package com.cryptaur.lottery.buytickets;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.mytickets.MyTicketViewHolder;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.util.Strings;

import org.threeten.bp.ZoneId;

import java.util.Locale;

public class DrawDetailsViewHolder extends RecyclerView.ViewHolder {
    private final TextView drawDetails;
    private Draw draw;

    public DrawDetailsViewHolder(ViewGroup view) {
        super(view);
        drawDetails = view.findViewById(R.id.details);
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
        Locale locale = Locale.getDefault();
        StringBuilder builder = new StringBuilder();
        String type = String.format(locale, "%dX%d\n", draw.lottery.getNumbersAmount(), draw.lottery.getMaxValue());
        String time = MyTicketViewHolder.dateTimeFormat.format(draw.startTime.atZone(ZoneId.systemDefault()));

        builder.append(type)
                .append(String.format(locale, "%d\n", draw.number))
                .append(time).append("\n");
        for (int i = 0; i < draw.numbers.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(String.format(locale, "%d", draw.numbers[i]));
        }
        builder.append("\n");
        builder.append(String.format(locale, "%d\n", draw.ticketsBought))
                .append(Strings.toDecimalString(draw.getCollected(), 8, 3, ".", ",")).append('\n')
                .append(Strings.toDecimalString(draw.paid, 8, 3, ".", ",")).append('\n')
                .append(Strings.toDecimalString(draw.jackpotAdded, 8, 3, ".", ",")).append('\n')
                .append(Strings.toDecimalString(draw.reserveAdded, 8, 3, ".", ",")).append('\n')
                .append(Strings.toDecimalString(draw.jackpot, 8, 3, ".", ",")).append('\n')
                .append(Strings.toDecimalString(draw.reserve, 8, 3, ".", ","));

        drawDetails.setText(builder);
    }
}
