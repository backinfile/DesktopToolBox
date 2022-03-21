package com.backinfile.toolBox;

import com.backinfile.support.Utils;
import com.backinfile.support.func.Action0;
import com.backinfile.support.func.Function0;
import com.backinfile.support.timer.TimerQueue;

public class Task {

    private static final TimerQueue timerQueue = new TimerQueue();

    static {
        Thread thread = new Thread(Task::runTimerQueue);
        thread.setDaemon(true);
        thread.start();
    }

    private static void runTimerQueue() {
        while (true) {
            timerQueue.update();
            Utils.sleep(Config.TIME_DELTA);
        }
    }

    public static void run(Action0 action) {
        timerQueue.applyTimer(0, action);
    }

    public static void schedule(long interval, Action0 action) {
        timerQueue.applyTimer(interval, action);
    }

    public static void schedule(long interval, Function0<Boolean> action) {
        final long[] timeId = new long[1];
        timeId[0] = timerQueue.applyTimer(interval, 0, () -> {
            if (!action.invoke()) {
                timerQueue.removeTimer(timeId[0]);
            }
        });
    }
}
