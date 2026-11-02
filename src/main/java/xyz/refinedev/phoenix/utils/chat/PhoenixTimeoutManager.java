package xyz.refinedev.phoenix.utils.chat;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import xyz.refinedev.lib.conversations.base.timeout.TimeoutScheduler;
import xyz.refinedev.lib.conversations.base.timeout.TimeoutTask;
import xyz.refinedev.lib.folialib.wrapper.task.WrappedTask;
import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhoenixTimeoutManager implements TimeoutScheduler {

    private final Phoenix plugin;

    private static final String text = "Timeout task threw: ";

    private static final long time = 50L;

    public TimeoutTask schedule(
            @NotNull Runnable runnable, long value, @NotNull TimeUnit timeUnit) {
        long value2 = Math.max(1L, timeUnit.toMillis(value) / time);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Runnable runnable2 =
                () -> {
                    atomicBoolean.set(true);
                    try {
                        runnable.run();
                    } catch (Throwable throwable) {
                        this.plugin.getLogger().warning(text + throwable.getMessage());
                    }
                };
        if (Tasks.foliaLib != null && Tasks.foliaLib.isFolia()) {
            WrappedTask wrappedTask = Tasks.runLaterWrappedTask(runnable2, value2);
            return new PhoenixTimeoutManager.PhoenixTimeoutTask(
                    atomicBoolean,
                    () -> {
                        if (wrappedTask != null) {
                            wrappedTask.cancel();
                        }
                    });
        } else {
            BukkitTask bukkitTask =
                    Bukkit.getScheduler()
                            .runTaskLaterAsynchronously(this.plugin, runnable2, value2);
            return new PhoenixTimeoutManager.PhoenixTimeoutTask(atomicBoolean, bukkitTask::cancel);
        }
    }

    public PhoenixTimeoutManager(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    static class PhoenixTimeoutTask implements TimeoutTask {

        private final AtomicBoolean called;

        private final Runnable canceller;

        public boolean hasCalled() {
            return this.called.get();
        }

        public void cancel() {
            this.canceller.run();
        }

        public PhoenixTimeoutTask(AtomicBoolean atomicBoolean, Runnable runnable) {
            this.called = atomicBoolean;
            this.canceller = runnable;
        }
    }
}
