package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.lib.snakeyml.Yaml;
import xyz.refinedev.phoenix.config.GlobalConfig;

import java.util.Collections;
import java.util.Map;

public final class DatabaseSettings {

    private final boolean enabled;

    private final String text;

    private final String textString;

    private final String text2;

    private final int count;

    private final String text3;

    private final String text4;

    private final String text5;

    private final String text6;

    private final int countCount;

    private final String text7;

    private final String text8;

    private final int count2;

    private final boolean enabledEnabled;

    private final boolean enabled2;

    private final boolean enabled3;

    private final boolean enabled4;

    private DatabaseSettings(Map<?, ?> entries) {
        this.enabled =
                formatString(entries, "database.database-type", GlobalConfig.DATABASE.DATABASE_TYPE)
                        .equalsIgnoreCase("MySQL");
        this.text =
                formatString(
                        matchesEnabled(
                                entries,
                                "database.mongo.uri-mode",
                                GlobalConfig.DATABASE.MONGO.URI_MODE),
                        formatString(
                                entries,
                                "database.mongo.mongo-uri",
                                GlobalConfig.DATABASE.MONGO.MONGO_URI),
                        matchesEnabled(
                                entries,
                                "database.mongo.authentication",
                                GlobalConfig.DATABASE.MONGO.AUTHENTICATION),
                        formatString(
                                entries,
                                "database.mongo.username",
                                GlobalConfig.DATABASE.MONGO.USERNAME),
                        formatString(
                                entries,
                                "database.mongo.password",
                                GlobalConfig.DATABASE.MONGO.PASSWORD),
                        formatString(
                                entries,
                                "database.mongo.address",
                                GlobalConfig.DATABASE.MONGO.ADDRESS),
                        calculateCount(
                                entries, "database.mongo.port", GlobalConfig.DATABASE.MONGO.PORT));
        this.textString =
                formatString(
                        entries, "database.mongo.database", GlobalConfig.DATABASE.MONGO.DATABASE);
        this.text2 =
                formatString(
                        entries, "database.mysql.address", GlobalConfig.DATABASE.MYSQL.ADDRESS);
        this.count =
                calculateCount(entries, "database.mysql.port", GlobalConfig.DATABASE.MYSQL.PORT);
        this.text3 =
                formatString(
                        entries, "database.mysql.database", GlobalConfig.DATABASE.MYSQL.DATABASE);
        this.text4 =
                formatString(
                        entries, "database.mysql.username", GlobalConfig.DATABASE.MYSQL.USERNAME);
        this.text5 =
                formatString(
                        entries, "database.mysql.password", GlobalConfig.DATABASE.MYSQL.PASSWORD);
        this.text6 = formatString(entries, "redis.address", GlobalConfig.REDIS.ADDRESS);
        this.countCount = calculateCount(entries, "redis.port", GlobalConfig.REDIS.PORT);
        this.text7 = formatString(entries, "redis.username", GlobalConfig.REDIS.USERNAME);
        this.text8 = formatString(entries, "redis.password", GlobalConfig.REDIS.PASSWORD);
        this.count2 = calculateCount(entries, "redis.database", GlobalConfig.REDIS.DATABASE);
        this.enabledEnabled = matchesEnabled(entries, "redis.ssl", GlobalConfig.REDIS.SSL);
        this.enabled2 =
                matchesEnabled(entries, "redis.verify-peer", GlobalConfig.REDIS.VERIFY_PEER);
        this.enabled3 = matchesEnabled(entries, "redis.start-tls", GlobalConfig.REDIS.START_TLS);
        this.enabled4 = matchesEnabled(entries, "redis.enabled", GlobalConfig.REDIS.ENABLED);
    }

    public static DatabaseSettings of(String text) {
        Object result = new Yaml().load(text);
        return new DatabaseSettings(result instanceof Map ? (Map) result : Collections.emptyMap());
    }

    public static String formatString(
            boolean matches,
            String text,
            boolean matches2,
            String text2,
            String text3,
            String text4,
            int value) {
        if (matches) {
            return text;
        } else {
            return matches2
                    ? "mongodb://" + text2 + ":" + text3 + "@" + text4 + ":" + value
                    : "mongodb://" + text4 + ":" + value;
        }
    }

    private static Object split(Map<?, ?> entries, String text) {
        Object result = entries;
        String[] texts;
        int value = (texts = text.split("\\.")).length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            if (!(result instanceof Map)) {
                return null;
            }
            result = ((Map) result).get(text2);
        }
        return result;
    }

    private static String formatString(Map<?, ?> entries, String text, String text2) {
        Object result;
        return (result = split(entries, text)) == null ? text2 : String.valueOf(result);
    }

    private static int calculateCount(Map<?, ?> entries, String text, int value) {
        Object result;
        if ((result = split(entries, text)) instanceof Number) {
            return ((Number) result).intValue();
        }
        if (result == null) {
            return value;
        }
        try {
            return Integer.parseInt(String.valueOf(result).trim());
        } catch (NumberFormatException numberFormatException) {
            return value;
        }
    }

    private static boolean matchesEnabled(Map<?, ?> entries, String text, boolean matches) {
        Object result;
        if ((result = split(entries, text)) instanceof Boolean) {
            return (Boolean) result;
        } else {
            return result == null ? matches : Boolean.parseBoolean(String.valueOf(result).trim());
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getText() {
        return this.text;
    }

    public String getTextString() {
        return this.textString;
    }

    public String getText2() {
        return this.text2;
    }

    public int getCount() {
        return this.count;
    }

    public String getText3() {
        return this.text3;
    }

    public String getText4() {
        return this.text4;
    }

    public String getText5() {
        return this.text5;
    }

    public String getText6() {
        return this.text6;
    }

    public int getCountCount() {
        return this.countCount;
    }

    public String getText7() {
        return this.text7;
    }

    public String getText8() {
        return this.text8;
    }

    public int getCount2() {
        return this.count2;
    }

    public boolean isEnabledEnabled() {
        return this.enabledEnabled;
    }

    public boolean isEnabled2() {
        return this.enabled2;
    }

    public boolean isEnabled3() {
        return this.enabled3;
    }

    public boolean isEnabled4() {
        return this.enabled4;
    }
}
