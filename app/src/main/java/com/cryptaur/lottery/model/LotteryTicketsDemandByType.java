package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.TicketsType;

public class LotteryTicketsDemandByType extends LotteryTicketsDemand {
    public final TicketsType type;
    public final int minAmount;

    public LotteryTicketsDemandByType(TicketsType type, int minAmount, SimpleGetObjectCallback<ITicketStorageRead> listener) {
        super(listener);
        this.type = type;
        this.minAmount = minAmount;
    }
}
