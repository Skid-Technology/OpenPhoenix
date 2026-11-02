package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.lib.io.lettuce.core.RedisClient;
import xyz.refinedev.lib.io.lettuce.core.RedisFuture;
import xyz.refinedev.lib.io.lettuce.core.RedisURI;
import xyz.refinedev.lib.io.lettuce.core.RedisURI.Builder;
import xyz.refinedev.lib.io.lettuce.core.api.StatefulRedisConnection;
import xyz.refinedev.lib.io.lettuce.core.api.async.RedisAsyncCommands;
import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;
import xyz.refinedev.lib.io.lettuce.core.metrics.CommandLatencyCollector;
import xyz.refinedev.lib.io.lettuce.core.resource.ClientResources;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.local.LocalRedis;
import xyz.refinedev.phoenix.handler.database.local.LocalRedisStore;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.ExecutorUtils;
import xyz.refinedev.phoenix.utils.RedisCommand;
import xyz.refinedev.phoenix.utils.VoidAsyncRedisCommand;
import xyz.refinedev.phoenix.utils.VoidRedisCommand;
import xyz.refinedev.phoenix.utils.repository.DataAccessFailures;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class AbstractDatabaseHandler implements IDatabaseHandler {

    protected final CommonPlatform platform;

    private RedisClient redisClient;

    private StatefulRedisConnection<String, String> redisConnection;

    private LocalRedis localRedis;

    public static ExecutorService DB_EXECUTOR = createDbExecutor();

    protected AbstractDatabaseHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        synchronized (AbstractDatabaseHandler.class) {
            if (DB_EXECUTOR.isShutdown()) {
                DB_EXECUTOR = createDbExecutor();
            }
        }
    }

    private static ExecutorService createDbExecutor() {
        int value = Math.min(16, Math.max(8, Runtime.getRuntime().availableProcessors() * 2));
        ThreadPoolExecutor threadPoolExecutor;
        (threadPoolExecutor =
                        new ThreadPoolExecutor(
                                value,
                                value,
                                60L,
                                TimeUnit.SECONDS,
                                new LinkedBlockingQueue<>(),
                                ExecutorUtils.create(
                                                "Phoenix - Database %d",
                                                5, true, PlatformGetter.getInstance().getLogger())
                                        .build()))
                .allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    public boolean setupRedis() {
        if (!GlobalConfig.REDIS.ENABLED) {
            File file = this.localStoreFile();
            try {
                LocalRedis localRedis2;
                (localRedis2 =
                                new LocalRedis(
                                        new LocalRedisStore(),
                                        this.platform.getLogger(),
                                        file,
                                        this.platform.getGson()))
                        .initialize();
                this.localRedis = localRedis2;
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "Could not start the local redis store from "
                                        + file.getPath()
                                        + ". Fix or remove that file, or turn redis.enabled back on in plugins/Phoenix/global.yml.",
                                exception);
                return false;
            }
            this.platform
                    .getLogger()
                    .info(
                            "Redis is off in global.yml, so shared state stays on this server. This only works for a single server.");
            return true;
        } else {
            try {
                String text = GlobalConfig.REDIS.ADDRESS;
                int value = GlobalConfig.REDIS.PORT;
                int value2 = GlobalConfig.REDIS.DATABASE;
                String text2 =
                        !GlobalConfig.REDIS.USERNAME.isEmpty()
                                ? GlobalConfig.REDIS.USERNAME
                                : "default";
                String text3 = GlobalConfig.REDIS.PASSWORD;
                Builder builder2 = RedisURI.builder().withHost(text).withPort(value);
                if (!text3.isEmpty()) {
                    builder2.withAuthentication(text2, text3);
                }
                if (GlobalConfig.REDIS.SSL) {
                    builder2.withSsl(true);
                    builder2.withVerifyPeer(GlobalConfig.REDIS.VERIFY_PEER);
                    if (GlobalConfig.REDIS.START_TLS) {
                        builder2.withStartTls(true);
                    }
                }
                builder2.withDatabase(value2);
                ClientResources clientResources =
                        ClientResources.builder()
                                .commandLatencyCollector(CommandLatencyCollector.disabled())
                                .build();
                this.redisClient = RedisClient.create(clientResources, builder2.build());
                this.redisConnection = this.redisClient.connect();
                try {
                    this.redisConnection.sync().ping();
                } catch (Throwable throwable) {
                    this.formatRedisThrowable(throwable);
                    return false;
                }
                try {
                    this.redisConnection.sync().del(new String[] {"px-profiles"});
                } catch (Exception exception2) {
                    this.platform
                            .getLogger()
                            .warning(
                                    "Failed to clean stale px-profiles hash: "
                                            + exception2.getClass().getSimpleName()
                                            + ": "
                                            + exception2.getMessage()
                                            + ". Profile entries written by an older build may survive and be served in preference to the database.");
                }
                this.platform.getLogger().info("Connected to Redis successfully!");
                return true;
            } catch (Exception exception3) {
                this.formatRedisThrowable(exception3);
                return false;
            }
        }
    }

    @Override
    public void disable() {
        if (this.redisConnection != null) {
            this.redisConnection.close();
        }
        if (this.redisClient != null) {
            this.redisClient.shutdown();
        }
        ExecutorUtils.applyExecutorService(DB_EXECUTOR);
        if (this.localRedis != null) {
            this.localRedis.initializeByScheduler();
        }
    }

    protected File localStoreFile() {
        return new File("plugins/Phoenix", "local-store.json");
    }

    private RedisCommands<String, String> sync() {
        return this.localRedis != null
                ? this.localRedis.getRedisCommands()
                : this.redisConnection.sync();
    }

    private RedisAsyncCommands<String, String> async() {
        return this.localRedis != null
                ? this.localRedis.getRedisAsyncCommands()
                : this.redisConnection.async();
    }

    @Override
    public boolean isRedisLocal() {
        return this.localRedis != null;
    }

    @Override
    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        RedisCommands<String, String> redisCommands = this.sync();
        try {
            T result = redisCommand.execute(redisCommands);
            DataAccessFailures.recordSuccess("redis");
            return (T) result;
        } catch (Exception exception) {
            this.recordRedisFailure("runCommand", exception);
            return null;
        }
    }

    @Override
    public void runRedisCommandAsync(VoidAsyncRedisCommand voidAsyncRedisCommand) {
        RedisAsyncCommands<String, String> redisAsyncCommands = this.async();
        RedisFuture<?> redisFuture = voidAsyncRedisCommand.execute(redisAsyncCommands);
        redisFuture.exceptionally(
                result -> {
                    this.recordRedisFailure("runCommandAsync", result);
                    return null;
                });
    }

    @Override
    public void executeRedisCommand(VoidRedisCommand voidRedisCommand) {
        RedisCommands<String, String> redisCommands = this.sync();
        try {
            voidRedisCommand.execute(redisCommands);
            DataAccessFailures.recordSuccess("redis");
        } catch (Exception exception) {
            this.recordRedisFailure("executeCommand", exception);
        }
    }

    @Override
    public void executeRedisCommand(
            VoidRedisCommand voidRedisCommand, Consumer<Throwable> consumer) {
        RedisCommands<String, String> redisCommands = this.sync();
        try {
            voidRedisCommand.execute(redisCommands);
            DataAccessFailures.recordSuccess("redis");
        } catch (Exception exception) {
            this.recordRedisFailure("executeCommand", exception);
            consumer.accept(exception);
        }
    }

    private void recordRedisFailure(String text, Throwable throwable) {
        DataAccessFailures.record("redis", text, "redis", null, ErrorReporter.getCause(throwable));
    }

    public void formatRedisThrowable(Throwable throwable) {
        this.recordRedisFailure("connect", throwable);
        this.logConnectFailure("redis", throwable);
    }

    protected void logConnectFailure(String text, Throwable throwable) {
        Throwable cause = ErrorReporter.getCause(throwable);
        String text2 = DatabaseErrorClassifier.formatString(throwable);
        String text3 = text.equals("redis") ? "redis" : "database." + text;
        String text4 =
                cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
        this.platform.getLogger().severe("Could not connect to " + text + ": " + text4);
        if (text2 != null) {
            this.platform.getLogger().severe(text2);
        }
        this.platform
                .getLogger()
                .severe(
                        "Check the "
                                + text3
                                + " section of plugins/Phoenix/global.yml. Guide: "
                                + "https://docs.refinedev.org/phoenix/database/");
        this.platform.getLogger().log(Level.SEVERE, "Full error:", cause);
    }

    @Override
    public long pingRedisMillis() {
        if (this.localRedis != null) {
            return 0L;
        }
        if (this.redisConnection == null) {
            return -1L;
        }
        long value = System.nanoTime();
        try {
            this.redisConnection.sync().ping();
        } catch (Exception exception) {
            return -1L;
        }
        return (System.nanoTime() - value) / 1000000L;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public StatefulRedisConnection<String, String> getRedisConnection() {
        return this.redisConnection;
    }

    public LocalRedis getLocalRedis() {
        return this.localRedis;
    }
}
