package com.cryptaur.lottery.model;

import com.cryptaur.lottery.transport.model.DrawId;

public class LotteryTicketsDemandByDraw extends LotteryTicketsDemand {
    DrawId drawId;

    public LotteryTicketsDemandByDraw(DrawId drawId, SimpleGetObjectCallback<ITicketStorageRead> listener) {
        super(listener);
        this.drawId = drawId;
    }
}
