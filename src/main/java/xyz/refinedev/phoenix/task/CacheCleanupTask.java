package xyz.refinedev.phoenix.task;

import xyz.refinedev.phoenix.Phoenix;

import java.util.logging.Level;

public class CacheCleanupTask implements Runnable {

    private final Phoenix plugin;

    private static final String text = "Could not release a due ban wave: ";

    @Override
    public void run() {
        try {
            this.plugin.getBanWaveHandler().releaseDue();
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, text, exception);
        }
    }

    public CacheCleanupTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
