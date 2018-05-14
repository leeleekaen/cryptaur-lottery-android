package com.cryptaur.lottery.buytickets;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.mytickets.MyTicketViewHolder;
import com.cryptaur.lottery.transport.model.Draw;

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
                .append(CPT.toDecimalString(draw.getCollected())).append('\n')
                .append(CPT.toDecimalString(draw.paid)).append('\n')
                .append(CPT.toDecimalString(draw.jackpotAdded)).append('\n')
                .append(CPT.toDecimalString(draw.reserveAdded)).append('\n')
                .append(CPT.toDecimalString(draw.jackpot)).append('\n')
                .append(CPT.toDecimalString(draw.reserve));

        drawDetails.setText(builder);
    }
}
