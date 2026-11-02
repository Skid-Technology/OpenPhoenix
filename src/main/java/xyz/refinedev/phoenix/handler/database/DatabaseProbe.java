package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.com.mysql.cj.jdbc.Driver;
import xyz.refinedev.lib.io.lettuce.core.RedisClient;
import xyz.refinedev.lib.io.lettuce.core.RedisURI;
import xyz.refinedev.lib.io.lettuce.core.RedisURI.Builder;
import xyz.refinedev.lib.io.lettuce.core.api.StatefulRedisConnection;
import xyz.refinedev.lib.mongodb.ConnectionString;
import xyz.refinedev.lib.mongodb.MongoClientSettings;
import xyz.refinedev.lib.mongodb.client.MongoClient;
import xyz.refinedev.lib.mongodb.client.MongoClients;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class DatabaseProbe {

    private DatabaseProbe() {}

    public static List<DatabaseProbeResult> findList(DatabaseSettings databaseSettings) {
        ArrayList<DatabaseProbeResult> values;
        (values = new ArrayList())
                .add(
                        databaseSettings.isEnabled()
                                ? findDatabaseProbeResultByDatabaseProbeResult(databaseSettings)
                                : findDatabaseProbeResult(databaseSettings));
        values.add(
                databaseSettings.isEnabled4()
                        ? findDatabaseProbeResultByDatabaseSettings(databaseSettings)
                        : DatabaseProbeResult.of(
                                "Redis", "Off. This server keeps shared state itself."));
        return values;
    }

    public static DatabaseProbeResult findDatabaseProbeResult(DatabaseSettings databaseSettings) {
        long value = System.nanoTime();
        try {
            MongoClient mongoClient =
                    MongoClients.create(
                            MongoClientSettings.builder()
                                    .applyConnectionString(
                                            new ConnectionString(databaseSettings.getText()))
                                    .applyToClusterSettings(
                                            result ->
                                                    result.serverSelectionTimeout(
                                                            5000L, TimeUnit.MILLISECONDS))
                                    .applyToSocketSettings(
                                            result ->
                                                    result.connectTimeout(
                                                            5000L, TimeUnit.MILLISECONDS))
                                    .build());
            DatabaseProbeResult databaseProbeResult;
            try {
                mongoClient
                        .getDatabase(databaseSettings.getTextString())
                        .runCommand(new Document("ping", 1));
                databaseProbeResult = DatabaseProbeResult.of("MongoDB", nanoTime(value));
            } catch (Throwable throwable) {
                if (mongoClient != null) {
                    try {
                        mongoClient.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            if (mongoClient != null) {
                mongoClient.close();
            }
            return databaseProbeResult;
        } catch (Exception exception) {
            return DatabaseProbeResult.of("MongoDB", exception);
        }
    }

    public static DatabaseProbeResult findDatabaseProbeResultByDatabaseProbeResult(
            DatabaseSettings databaseSettings) {
        long value = System.nanoTime();
        Properties properties;
        (properties = new Properties()).setProperty("user", databaseSettings.getText4());
        properties.setProperty("password", databaseSettings.getText5());
        properties.setProperty("connectTimeout", String.valueOf(5000));
        properties.setProperty("socketTimeout", String.valueOf(5000));
        String text =
                "jdbc:mysql://"
                        + databaseSettings.getText2()
                        + ":"
                        + databaseSettings.getCount()
                        + "/"
                        + databaseSettings.getText3();
        try {
            Connection connection = new Driver().connect(text, properties);
            DatabaseProbeResult databaseProbeResult;
            label50:
            {
                try {
                    if (!connection.isValid(5)) {
                        databaseProbeResult =
                                DatabaseProbeResult.of(
                                        "MySQL",
                                        new SQLException(
                                                "The connection opened but did not answer a validity check."));
                        break label50;
                    }
                    databaseProbeResult = DatabaseProbeResult.of("MySQL", nanoTime(value));
                } catch (Throwable throwable) {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                if (connection != null) {
                    connection.close();
                }
                return databaseProbeResult;
            }
            if (connection != null) {
                connection.close();
            }
            return databaseProbeResult;
        } catch (Exception exception) {
            return DatabaseProbeResult.of("MySQL", exception);
        }
    }

    public static DatabaseProbeResult findDatabaseProbeResultByDatabaseSettings(
            DatabaseSettings databaseSettings) {
        long value = System.nanoTime();
        RedisClient redisClient = null;
        DatabaseProbeResult databaseProbeResult;
        try {
            Builder builder2 =
                    RedisURI.builder()
                            .withHost(databaseSettings.getText6())
                            .withPort(databaseSettings.getCountCount())
                            .withDatabase(databaseSettings.getCount2())
                            .withTimeout(Duration.ofMillis(5000L));
            if (!databaseSettings.getText8().isEmpty()) {
                String text =
                        databaseSettings.getText7().isEmpty()
                                ? "default"
                                : databaseSettings.getText7();
                builder2.withAuthentication(text, databaseSettings.getText8());
            }
            if (databaseSettings.isEnabledEnabled()) {
                builder2.withSsl(true).withVerifyPeer(databaseSettings.isEnabled2());
                if (databaseSettings.isEnabled3()) {
                    builder2.withStartTls(true);
                }
            }
            StatefulRedisConnection statefulRedisConnection =
                    (redisClient = RedisClient.create(builder2.build())).connect();
            try {
                statefulRedisConnection.sync().ping();
                databaseProbeResult = DatabaseProbeResult.of("Redis", nanoTime(value));
            } catch (Throwable throwable) {
                if (statefulRedisConnection != null) {
                    try {
                        statefulRedisConnection.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            if (statefulRedisConnection != null) {
                statefulRedisConnection.close();
            }
        } catch (Exception exception) {
            return DatabaseProbeResult.of("Redis", exception);
        } finally {
            if (redisClient != null) {
                redisClient.shutdown();
            }
        }
        return databaseProbeResult;
    }

    private static long nanoTime(long value) {
        return (System.nanoTime() - value) / 1000000L;
    }
}
