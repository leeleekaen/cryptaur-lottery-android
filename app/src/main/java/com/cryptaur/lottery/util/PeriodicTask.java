package com.cryptaur.lottery.util;

import android.os.Handler;

public class PeriodicTask {
    private final Handler handler;
    private final Runnable runnable;
    private final boolean isThreadSafe;
    private long timeout;
    private volatile boolean running;
    private boolean shouldRun;
    private boolean canRun;

    /**
     * @param handler      -- handler to be run on
     * @param timeout      -- timeout to repeat task
     * @param isThreadSafe -- set to true if setRunning can be run on threads other then handler's thread
     * @param runnable     -- task that will be executed
     */
    public PeriodicTask(Handler handler, long timeout, boolean isThreadSafe, Runnable runnable) {
        this.handler = handler;
        this.runnable = runnable;
        this.timeout = timeout;
        this.isThreadSafe = isThreadSafe;
    }

    public void updateRunState() {
        if (!isThreadSafe && handler.getLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("should run on the same thread as handler runs");
        }
        if (isThreadSafe) {
            synchronized (this) {
                if (!running && shouldRun && canRun) {
                    running = true;
                    handler.postDelayed(this::run, timeout);
                }
            }
        } else if (!running && shouldRun && canRun) {
            running = true;
            handler.postDelayed(this::run, timeout);
        }
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public void setCanRun(boolean canRun) {
        this.canRun = canRun;
    }

    public void scheduleToRun() {
        setShouldRun(true);
        updateRunState();
    }

    public boolean isRunning() {
        return running;
    }

    private void run() {
        if (shouldRun & canRun) {
            runnable.run();
            setRunning(true);
            handler.postDelayed(this::run, timeout);
        } else {
            setRunning(false);
        }
    }

    private void setRunning(boolean running) {
        if (isThreadSafe) {
            if (this.running != running)
                synchronized (this) {
                    this.running = running;
                }
        } else {
            this.running = running;
        }
    }
}
