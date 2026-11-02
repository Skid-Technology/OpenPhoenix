package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.io.lettuce.core.RedisClient;
import xyz.refinedev.phoenix.utils.RedisCommand;
import xyz.refinedev.phoenix.utils.VoidAsyncRedisCommand;
import xyz.refinedev.phoenix.utils.VoidRedisCommand;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public interface IDatabaseHandler {

    boolean enable();

    void disable();

    <T> T runRedisCommand(RedisCommand<T> redisCommand);

    void runRedisCommandAsync(VoidAsyncRedisCommand voidAsyncRedisCommand);

    void executeRedisCommand(VoidRedisCommand voidRedisCommand);

    void executeRedisCommand(VoidRedisCommand voidRedisCommand, Consumer<Throwable> consumer);

    RedisClient getRedisClient();

    long pingRedisMillis();

    default boolean isRedisLocal() {
        return false;
    }

    default Map<String, Long> getPoolStats() {
        return Collections.emptyMap();
    }
}
