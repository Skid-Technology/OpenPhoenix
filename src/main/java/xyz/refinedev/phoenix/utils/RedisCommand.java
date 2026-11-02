package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;

public interface RedisCommand<T> {

    T execute(RedisCommands<String, String> redisCommands);
}
