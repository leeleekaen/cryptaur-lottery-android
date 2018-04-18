package com.cryptaur.lottery.transport;

import android.os.Handler;

import com.cryptaur.lottery.transport.model.Session;

public class SessionRefresher {

    private final Handler handler;
    private final RefresherListener listener;
    SessionRefresherTask currentTask;
    private boolean canRefresh = false;

    public SessionRefresher(Handler handler, RefresherListener listener) {
        this.handler = handler;
        this.listener = listener;
    }

    public void postponeRefresh(Session session) {
        long delay = session.getExpireTimestamp() - System.currentTimeMillis();
        SessionRefresherTask task = currentTask;
        if (task != null) {
            task.cancel();
        }
        currentTask = new SessionRefresherTask(false);
        handler.postDelayed(currentTask, delay);
    }

    public void refreshSession() {
        if (canRefresh) {
            SessionRefresherTask task = currentTask;
            if (task != null) {
                task.cancel();
            }
            currentTask = null;
            listener.refreshSession();
        }
    }

    public void onResumeActivity() {
        this.canRefresh = true;
    }

    public void onPauseActivity() {
        this.canRefresh = false;
        handler.postDelayed(new SessionRefresherTask(true), 1000);

    }

    public interface RefresherListener {
        void refreshSession();
    }

    private class SessionRefresherTask implements Runnable {
        private final boolean invertCanRefresh;
        private volatile boolean cancelled;

        public SessionRefresherTask(boolean invertCanRefresh) {
            this.invertCanRefresh = invertCanRefresh;
        }

        @Override
        public void run() {
            if (!cancelled) {
                if (canRefresh != invertCanRefresh) {
                    listener.refreshSession();
                }
                currentTask = null;
            }
        }

        void cancel() {
            cancelled = true;
        }
    }
}
