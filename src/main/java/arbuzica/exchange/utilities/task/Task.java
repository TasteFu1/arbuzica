package arbuzica.exchange.utilities.task;

import arbuzica.exchange.utilities.java.Timer;

public abstract class Task {
    private final Timer timer;
    private final long delayMillis;

    public Task(long delayMillis) {
        this.timer = new Timer();
        this.delayMillis = delayMillis;
    }

    public abstract void execute();

    public Timer getTimer() {
        return timer;
    }

    public long getDelayMillis() {
        return delayMillis;
    }
}
