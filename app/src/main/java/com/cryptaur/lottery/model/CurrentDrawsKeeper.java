package com.cryptaur.lottery.model;

import android.support.annotation.Nullable;

import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.DrawIds;

import io.paperdb.Paper;

import static com.cryptaur.lottery.model.Keeper.DRAWS_UPDATE_TIMEOUT;

public class CurrentDrawsKeeper extends SimpleItemKeeper<CurrentDraws> {

    private static final String KEY_LATEST_PLAYED_DRAWS = "latestPlayedDraws";
    private final CallbackList<OnPlayedDrawsChangedListener> onPlayedDrawsChangedListeners = new CallbackList<>();
    private DrawIds playedDrawIds;

    CurrentDrawsKeeper() {
        super(DRAWS_UPDATE_TIMEOUT, Transport.INSTANCE::getLotteries);
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, CurrentDraws responce) {
        final DrawIds newPlayedDraws = responce.latestPlayedDraws();
        final DrawIds oldPlayedDraws = getPlayedDraws();
        if (!newPlayedDraws.equals(oldPlayedDraws)) {
            playedDrawIds = newPlayedDraws;
            Paper.book().write(KEY_LATEST_PLAYED_DRAWS, newPlayedDraws);
            onPlayedDrawsChangedListeners.notifyAllCallbacks(l -> l.onPlayedDrawsChanged(oldPlayedDraws, newPlayedDraws, responce));
        } else if (value == null) {
            onPlayedDrawsChangedListeners.notifyAllCallbacks(l -> l.onPlayedDrawsChanged(oldPlayedDraws, newPlayedDraws, responce));
        }
        super.onNetworkRequestDone(request, responce);
    }

    private DrawIds getPlayedDraws() {
        if (playedDrawIds != null)
            return playedDrawIds;

        return playedDrawIds = Paper.book().read(KEY_LATEST_PLAYED_DRAWS);
    }

    public void addOnPlayedDrawsChangedListener(OnPlayedDrawsChangedListener listener) {
        onPlayedDrawsChangedListeners.add(listener);
    }

    public void removeOnPlayedDrawsChangedListener(OnPlayedDrawsChangedListener listener) {
        onPlayedDrawsChangedListeners.remove(listener);
    }

    @Nullable
    public CurrentDraws getCurrentDraws() {
        return value;
    }

    public interface OnPlayedDrawsChangedListener {
        void onPlayedDrawsChanged(DrawIds oldPlayedDrawIds, DrawIds newPlayedDrawIds, CurrentDraws currentDraws);
    }
}
