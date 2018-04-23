package com.cryptaur.lottery;

public interface Const {
    String TAG = "CryptLottery";
    //String SERVER_URL = "https://lottery.cryptaur.com";
    boolean USE_TEST_SERVER = BuildConfig.DEBUG || true;
    String SERVER_URL = USE_TEST_SERVER ? "http://192.168.4.199:24892/" : "https://lottery-3.cryptaur.com/";
    String AUTH_URL = "https://lottery-1.cryptaur.com/";

    String KEY_ADDRESS = "address";
    String KEY_LATEST_PLAYED_VIEWED_INDICES = "viewedIndices";

    int GET_TICKETS_STEP = 10;
}
