package xyz.refinedev.phoenix.handler.database.local;

import com.google.gson.Gson;

import xyz.refinedev.lib.io.lettuce.core.api.async.RedisAsyncCommands;
import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;
import xyz.refinedev.lib.io.lettuce.core.pubsub.RedisPubSubListener;
import xyz.refinedev.phoenix.config.GlobalConfig;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalRedis {

    private final LocalRedisStore localRedisStore;

    private final RedisCommands<String, String> redisCommands;

    private final RedisAsyncCommands<String, String> redisAsyncCommands;

    private final Logger logger;

    private final File file;

    private final Gson gson;

    private final Map<String, List<RedisPubSubListener<String, String>>> stringsByName =
            new ConcurrentHashMap<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor(findThreadFactory("Phoenix - Local PubSub"));

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(findThreadFactory("Phoenix - Local Store"));

    public LocalRedis(LocalRedisStore localRedisStore2, Logger logger2, File file2, Gson gson2) {
        this.localRedisStore = localRedisStore2;
        this.logger = logger2;
        this.file = file2;
        this.gson = gson2;
        this.redisCommands =
                LocalRedisCommands.findRedisCommands(localRedisStore2, this::calculateTime);
        this.redisAsyncCommands =
                LocalRedisCommands.findRedisAsyncCommands(localRedisStore2, this::calculateTime);
    }

    public void initialize() {
        this.localRedisStore.applyLocalStorePersistence(
                LocalStorePersistence.findLocalStorePersistence(this.file, this.gson, this.logger));
        LocalRedis localRedis = this;
        this.scheduler.scheduleAtFixedRate(
                localRedis::initializeByLocalRedisStore, 5L, 5L, TimeUnit.MINUTES);
        LocalRedisStore localRedisStore2 = this.localRedisStore;
        this.scheduler.scheduleAtFixedRate(localRedisStore2::getCount, 60L, 60L, TimeUnit.SECONDS);
    }

    public void applyString(String text, RedisPubSubListener<String, String> redisPubSubListener) {
        this.stringsByName
                .computeIfAbsent(text, result -> new CopyOnWriteArrayList<>())
                .add(redisPubSubListener);
    }

    public long calculateTime(String text, String text2) {
        List<RedisPubSubListener<String, String>> values;
        if ((values = this.stringsByName.get(text)) != null && !values.isEmpty()) {
            for (RedisPubSubListener<String, String> redisPubSubListener : values) {
                this.executor.execute(
                        () -> {
                            try {
                                redisPubSubListener.message(text, text2);
                            } catch (Throwable throwable) {
                                this.logger.log(
                                        Level.WARNING,
                                        "A local packet listener failed.",
                                        throwable);
                            }
                        });
            }
            return values.size();
        } else {
            return 0L;
        }
    }

    public void initializeByScheduler() {
        this.scheduler.shutdownNow();
        try {
            this.scheduler.awaitTermination(5L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        this.initializeByLocalRedisStore();
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(5L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException2) {
            Thread.currentThread().interrupt();
        }
    }

    private void initializeByLocalRedisStore() {
        try {
            this.localRedisStore
                    .create(LocalStorePersistence.predicate)
                    .applyFile(this.file, this.gson);
        } catch (Exception exception) {
            this.logger.log(
                    Level.WARNING, "Could not save " + this.file.getName() + ".", exception);
        }
    }

    public static boolean matchesEnabled(Logger logger, String text) {
        if (GlobalConfig.REDIS.ENABLED) {
            return false;
        }
        logger.severe(
                "redis.enabled is false in global.yml, but "
                        + text
                        + " always needs redis. Turn it back on.");
        return true;
    }

    private static ThreadFactory findThreadFactory(String text) {
        return result -> {
            Thread thread;
            (thread = new Thread(result, text)).setDaemon(true);
            return thread;
        };
    }

    public LocalRedisStore getLocalRedisStore() {
        return this.localRedisStore;
    }

    public RedisCommands<String, String> getRedisCommands() {
        return this.redisCommands;
    }

    public RedisAsyncCommands<String, String> getRedisAsyncCommands() {
        return this.redisAsyncCommands;
    }
}
