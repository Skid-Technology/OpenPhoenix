package xyz.refinedev.phoenix.handler.database.local;

import xyz.refinedev.lib.io.lettuce.core.SetArgs;
import xyz.refinedev.lib.io.lettuce.core.api.async.RedisAsyncCommands;
import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;
import xyz.refinedev.lib.io.lettuce.core.codec.StringCodec;
import xyz.refinedev.lib.io.lettuce.core.protocol.CommandArgs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

public final class LocalRedisCommands implements InvocationHandler {

    private final LocalRedisStore localRedisStore;

    private final LocalRedisCommands.Publisher publisher;

    private final boolean enabled;

    private LocalRedisCommands(
            LocalRedisStore localRedisStore2,
            LocalRedisCommands.Publisher publisher2,
            boolean enabled) {
        this.localRedisStore = localRedisStore2;
        this.publisher = publisher2;
        this.enabled = enabled;
    }

    public static RedisCommands<String, String> findRedisCommands(
            LocalRedisStore localRedisStore, LocalRedisCommands.Publisher publisher) {
        return (RedisCommands<String, String>)
                Proxy.newProxyInstance(
                        LocalRedisCommands.class.getClassLoader(),
                        new Class[] {RedisCommands.class},
                        new LocalRedisCommands(localRedisStore, publisher, false));
    }

    public static RedisAsyncCommands<String, String> findRedisAsyncCommands(
            LocalRedisStore localRedisStore, LocalRedisCommands.Publisher publisher) {
        return (RedisAsyncCommands<String, String>)
                Proxy.newProxyInstance(
                        LocalRedisCommands.class.getClassLoader(),
                        new Class[] {RedisAsyncCommands.class},
                        new LocalRedisCommands(localRedisStore, publisher, true));
    }

    @Override
    public Object invoke(Object result, Method method, Object[] results) {
        if (method.getDeclaringClass() == Object.class) {
            switch (method.getName()) {
                case "equals":
                    return result == results[0];
                case "hashCode":
                    return System.identityHashCode(result);
                default:
                    return "LocalRedis" + (this.enabled ? "Async" : "Sync") + "Commands";
            }
        } else {
            Object[] results2 = results == null ? new Object[0] : results;
            if (!this.enabled) {
                return this.findObject(method.getName(), results2);
            }
            try {
                return LocalRedisFuture.of(this.findObject(method.getName(), results2));
            } catch (RuntimeException runtimeException) {
                return LocalRedisFuture.of(runtimeException);
            }
        }
    }

    private Object findObject(String text, Object[] results) {
        switch (text) {
            case "get":
                return this.localRedisStore.formatString((String) results[0]);
            case "set":
                if (results.length == 2) {
                    return this.localRedisStore.formatString(
                            (String) results[0], (String) results[1], 0L, false);
                }
                if (results.length == 3 && results[2] instanceof SetArgs) {
                    return this.formatString(
                            (String) results[0], (String) results[1], (SetArgs) results[2]);
                }
                break;
            case "setex":
                return this.localRedisStore.formatString(
                        (String) results[0],
                        (String) results[2],
                        ((Number) results[1]).longValue() * 1000L,
                        false);
            case "del":
                return this.localRedisStore.remove(findStringArray(results, 0));
            case "exists":
                return this.localRedisStore.calculateTime(findStringArray(results, 0));
            case "keys":
                if (results.length == 1) {
                    return this.localRedisStore.findList((String) results[0]);
                }
                break;
            case "expire":
                if (results.length == 2 && results[1] instanceof Number) {
                    return this.localRedisStore.getAsLong(
                            (String) results[0], ((Number) results[1]).longValue() * 1000L);
                }
                if (results.length == 2 && results[1] instanceof Duration) {
                    return this.localRedisStore.getAsLong(
                            (String) results[0], ((Duration) results[1]).toMillis());
                }
                break;
            case "flushdb":
                if (results.length == 0) {
                    return this.localRedisStore.clear();
                }
                break;
            case "hget":
                return this.localRedisStore.formatString((String) results[0], (String) results[1]);
            case "hset":
                if (results.length == 2 && results[1] instanceof Map) {
                    return this.localRedisStore.calculateTime(
                            (String) results[0], (Map<String, String>) results[1]);
                }
                if (results.length == 3) {
                    return this.localRedisStore.matchesEnabled(
                            (String) results[0], (String) results[1], (String) results[2]);
                }
                break;
            case "hsetnx":
                return this.localRedisStore.putIfAbsent(
                        (String) results[0], (String) results[1], (String) results[2]);
            case "hdel":
                return this.localRedisStore.calculateTime(
                        (String) results[0], findStringArray(results, 1));
            case "hgetall":
                if (results.length == 1) {
                    return this.localRedisStore.findMap((String) results[0]);
                }
                break;
            case "hkeys":
                if (results.length == 1) {
                    return this.localRedisStore.keySet((String) results[0]);
                }
                break;
            case "hlen":
                return this.localRedisStore.calculateTime((String) results[0]);
            case "hexists":
                return this.localRedisStore.containsKey((String) results[0], (String) results[1]);
            case "publish":
                return this.publisher.publish((String) results[0], (String) results[1]);
            case "ping":
                return "PONG";
        }
        throw new UnsupportedOperationException(
                "Redis command '"
                        + text
                        + "' is not available with redis.enabled: false. Turn redis back on in global.yml.");
    }

    private String formatString(String text, String text2, SetArgs setArgs) {
        CommandArgs commandArgs = new CommandArgs(StringCodec.UTF8);
        setArgs.build(commandArgs);
        String[] texts = commandArgs.toCommandString().trim().split("\\s+");
        long value = 0L;
        boolean matches = false;
        for (int index = 0; index < texts.length; index++) {
            String text3;
            if (!(text3 = texts[index].toUpperCase(Locale.ROOT)).isEmpty()) {
                switch (text3) {
                    case "EX":
                        value = Long.parseLong(texts[++index]) * 1000L;
                        break;
                    case "PX":
                        value = Long.parseLong(texts[++index]);
                        break;
                    case "NX":
                        matches = true;
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "SET option '"
                                        + text3
                                        + "' is not available with redis.enabled: false.");
                }
            }
        }
        return this.localRedisStore.formatString(text, text2, value, matches);
    }

    private static String[] findStringArray(Object[] results, int value) {
        if (results.length == value + 1 && results[value] instanceof String[]) {
            return (String[]) results[value];
        }
        String[] texts = new String[results.length - value];
        for (int value2 = value; value2 < results.length; value2++) {
            texts[value2 - value] = (String) results[value2];
        }
        return texts;
    }

    @FunctionalInterface
    public interface Publisher {

        long publish(String text, String text2);
    }
}
