package com.cryptaur.lottery.model;

class LotteryTicketsDemand {
    final SimpleGetObjectCallback<ITicketStorageRead> listener;

    public LotteryTicketsDemand(SimpleGetObjectCallback<ITicketStorageRead> listener) {
        this.listener = listener;
    }
}
