package com.cryptaur.lottery;

import java.math.BigInteger;

public interface Const {
    String TAG = "CryptLottery";
    //String SERVER_URL = "https://lottery.cryptaur.com";
    boolean USE_TEST_SERVER = BuildConfig.DEBUG || true;
    String SERVER_URL = USE_TEST_SERVER ? "http://192.168.4.199:24892/" : "https://lottery-3.cryptaur.com/";
    String AUTH_URL = "https://lottery-1.cryptaur.com/";

    String KEY_ADDRESS = "address";
    String KEY_LATEST_PLAYED_VIEWED_INDICES = "viewedIndices";

    long STOP_TICKET_SELL_INTERVAL_SEC = USE_TEST_SERVER ? 20 * 60 : 60 * 60;

    int GET_TICKETS_STEP = 10;

    BigInteger CPT_BASE = BigInteger.valueOf(100_000_000);

    String URL_CRYPTAUR_WALLET = "https://wallet.cryptaur.com/investor/login";
}
