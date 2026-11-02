package xyz.refinedev.phoenix.utils.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import xyz.refinedev.lib.folialib.FoliaLib;
import xyz.refinedev.lib.folialib.impl.PlatformScheduler;
import xyz.refinedev.lib.folialib.wrapper.task.WrappedTask;
import xyz.refinedev.phoenix.Phoenix;

public class Tasks {

    public static FoliaLib foliaLib;

    public static void run(JavaPlugin javaPlugin, Runnable runnable) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runNextTick(result -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(javaPlugin, runnable);
        }
    }

    public static void runAtEntity(JavaPlugin javaPlugin, Entity entity, Runnable runnable) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runAtEntity(entity, result -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(javaPlugin, runnable);
        }
    }

    public static void runAsync(JavaPlugin javaPlugin, Runnable runnable) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runAsync(result -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, runnable);
        }
    }

    public static void runLater(JavaPlugin javaPlugin, Runnable runnable, long value) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runLater(result -> runnable.run(), value);
        } else {
            Bukkit.getScheduler().runTaskLater(javaPlugin, runnable, value);
        }
    }

    public static int runLaterWithId(JavaPlugin javaPlugin, Runnable runnable, long value) {
        if (foliaLib != null && foliaLib.isFolia()) {
            throw new UnsupportedOperationException();
        } else {
            return Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, runnable, value);
        }
    }

    public static WrappedTask runLaterWrappedTask(Runnable runnable, long value) {
        if (foliaLib != null && foliaLib.isFolia()) {
            return foliaLib.getScheduler().runLater(runnable, value);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static WrappedTask runWrappedTimer(Runnable runnable, long value, long value2) {
        if (foliaLib != null && foliaLib.isFolia()) {
            return foliaLib.getScheduler().runTimer(runnable, value, value2);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Runnable runCancellableTimer(Runnable runnable, long value, long value2) {
        if (foliaLib != null && foliaLib.isFolia()) {
            WrappedTask wrappedTask = foliaLib.getScheduler().runTimer(runnable, value, value2);
            return wrappedTask::cancel;
        } else {
            BukkitTask bukkitTask =
                    Bukkit.getScheduler()
                            .runTaskTimer(Phoenix.getInstance(), runnable, value, value2);
            return bukkitTask::cancel;
        }
    }

    public static void runAsyncLater(JavaPlugin javaPlugin, Runnable runnable, long value) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runLaterAsync(result -> runnable.run(), value);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, runnable, value);
        }
    }

    public static void runTimer(JavaPlugin javaPlugin, Runnable runnable, long value, long value2) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runTimer(result -> runnable.run(), value, value2);
        } else {
            Bukkit.getScheduler().runTaskTimer(javaPlugin, runnable, value, value2);
        }
    }

    public static void runAsyncTimer(
            JavaPlugin javaPlugin, Runnable runnable, long value, long value2) {
        if (foliaLib != null && foliaLib.isFolia()) {
            PlatformScheduler scheduler = foliaLib.getScheduler();
            scheduler.runTimerAsync(result -> runnable.run(), value, value2);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, runnable, value, value2);
        }
    }

    public static void cancelTasks(WrappedTask wrappedTask, int value) {
        if (foliaLib != null && foliaLib.isFolia()) {
            foliaLib.getScheduler().cancelTask(wrappedTask);
        } else {
            Bukkit.getScheduler().cancelTask(value);
        }
    }

    public static void run(Runnable runnable) {
        run(Phoenix.getInstance(), runnable);
    }

    public static void runAtEntity(Entity entity, Runnable runnable) {
        runAtEntity(Phoenix.getInstance(), entity, runnable);
    }

    public static void runAsync(Runnable runnable) {
        runAsync(Phoenix.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, long value) {
        runLater(Phoenix.getInstance(), runnable, value);
    }

    public static void runAsyncLater(Runnable runnable, long value) {
        runAsyncLater(Phoenix.getInstance(), runnable, value);
    }

    public static void runTimer(Runnable runnable, long value, long value2) {
        runTimer(Phoenix.getInstance(), runnable, value, value2);
    }

    public static void runAsyncTimer(Runnable runnable, long value, long value2) {
        runAsyncTimer(Phoenix.getInstance(), runnable, value, value2);
    }
}
