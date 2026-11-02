package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.io.lettuce.core.RedisFuture;
import xyz.refinedev.lib.io.lettuce.core.api.async.RedisAsyncCommands;

public interface VoidAsyncRedisCommand {

    RedisFuture<?> execute(RedisAsyncCommands<String, String> redisAsyncCommands);
}
