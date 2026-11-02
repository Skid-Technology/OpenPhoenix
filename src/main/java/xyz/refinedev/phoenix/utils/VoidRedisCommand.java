package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;

public interface VoidRedisCommand {

    void execute(RedisCommands<String, String> redisCommands);
}
