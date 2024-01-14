package arbuzica.exchange.utilities.java;

public class Timer {
    private long lastMillis;

    public boolean loop(long delayMillis) {
        if (reach(delayMillis)) {
            reset();
            return true;
        }

        return false;
    }

    public boolean reach(long delayMillis) {
        return System.currentTimeMillis() > lastMillis + delayMillis;
    }

    public Timer reset() {
        lastMillis = System.currentTimeMillis();
        return this;
    }

    public long getLastMillis() {
        return lastMillis;
    }

    public long getDifference() {
        return System.currentTimeMillis() - lastMillis;
    }
}
