package xyz.refinedev.phoenix.nametag;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.utils.ExecutorUtils;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class NameTagExecutor {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(
                    1,
                    ExecutorUtils.create("Phoenix - Nametag Thread", 5, true, Bukkit.getLogger())
                            .build());

    public NameTagExecutor() {
        this.scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.scheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }

    public void applyRunnable(Runnable runnable) {
        try {
            this.scheduledThreadPoolExecutor.execute(findRunnable(runnable));
        } catch (RejectedExecutionException rejectedExecutionException) {
        }
    }

    public void applyRunnable(Runnable runnable, long value) {
        try {
            this.scheduledThreadPoolExecutor.schedule(
                    findRunnable(runnable), value * 50L, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException rejectedExecutionException) {
        }
    }

    public void applyTime(long value) {
        if (!ExecutorUtils.matchesEnabled(this.scheduledThreadPoolExecutor, value)) {
            Bukkit.getLogger()
                    .warning("[PhoenixNT] nametag worker did not finish in " + value + "ms");
        }
    }

    private static Runnable findRunnable(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                Bukkit.getLogger().warning("[PhoenixNT] nametag task failed: " + throwable);
            }
        };
    }
}
