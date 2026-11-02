package xyz.refinedev.phoenix.alt;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.login.Login;
import xyz.refinedev.phoenix.utils.mysql.SQLBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class LoginIndexMigration {

    private final CommonPlatform platform;

    private volatile boolean enabled;

    public LoginIndexMigration(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public void initialize() {
        String name = this.getDatabaseHandler().getLogins().getName();
        String name2 = this.getDatabaseHandler().getAltLinks().getName();
        try {
            if (!this.matchesEnabled(name, "ip")) {
                this.platform
                        .getLogger()
                        .info(
                                "(Alts) Adding indexed login columns to "
                                        + name
                                        + ". On a large table this runs once and may take a while.");
                SQLBuilder sQLBuilder = new SQLBuilder();
                try {
                    sQLBuilder.setStatement(
                            "ALTER TABLE "
                                    + name
                                    + " ADD COLUMN ip VARCHAR(96) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.ip'))) VIRTUAL, ADD COLUMN target VARCHAR(64) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.target'))) VIRTUAL, ADD INDEX pxlogins_ip (ip), ADD INDEX pxlogins_target (target)");
                    sQLBuilder.executeUpdate();
                } catch (Throwable throwable) {
                    try {
                        sQLBuilder.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                sQLBuilder.close();
                this.platform.getLogger().info("(Alts) Indexed login columns ready.");
            }
            if (!this.matchesEnabled(name2, "a")) {
                SQLBuilder sQLBuilder2 = new SQLBuilder();
                try {
                    sQLBuilder2.setStatement(
                            "ALTER TABLE "
                                    + name2
                                    + " ADD COLUMN a VARCHAR(36) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.a'))) VIRTUAL, ADD COLUMN b VARCHAR(36) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.b'))) VIRTUAL, ADD INDEX pxaltLinks_a (a), ADD INDEX pxaltLinks_b (b)");
                    sQLBuilder2.executeUpdate();
                } catch (Throwable throwable3) {
                    try {
                        sQLBuilder2.close();
                    } catch (Throwable throwable4) {
                        throwable3.addSuppressed(throwable4);
                    }
                    throw throwable3;
                }
                sQLBuilder2.close();
            }
            this.enabled = true;
        } catch (Exception exception) {
            this.platform
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "(Alts) Could not add indexed columns - alt queries will run unindexed.",
                            exception);
        }
    }

    public List<ILogin> findList(String text, Collection<String> values) {
        if (this.enabled && !values.isEmpty()) {
            String name = this.getDatabaseHandler().getLogins().getName();
            try {
                SQLBuilder sQLBuilder = new SQLBuilder();
                ArrayList<ILogin> values2;
                try {
                    sQLBuilder.setStatement(
                            "SELECT data FROM "
                                    + name
                                    + " WHERE "
                                    + text
                                    + " IN ("
                                    + this.append(values.size())
                                    + ")");
                    sQLBuilder.executeQuery(values.toArray());
                    ArrayList<ILogin> values3 = new ArrayList<>();
                    while (sQLBuilder.next()) {
                        ILogin login;
                        if ((login = this.findObject(sQLBuilder, Login.class)) != null) {
                            values3.add(login);
                        }
                    }
                    values2 = values3;
                } catch (Throwable throwable) {
                    try {
                        sQLBuilder.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                sQLBuilder.close();
                return values2;
            } catch (Exception exception) {
                this.platform
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "(Alts) Indexed login query failed, falling back to filters.",
                                exception);
                return null;
            }
        } else {
            return null;
        }
    }

    public List<IAltLink> findList(UUID uUID) {
        if (!this.enabled) {
            return null;
        }
        String name = this.getDatabaseHandler().getAltLinks().getName();
        try {
            SQLBuilder sQLBuilder = new SQLBuilder();
            ArrayList<IAltLink> values;
            try {
                sQLBuilder.setStatement("SELECT data FROM " + name + " WHERE a = ? OR b = ?");
                sQLBuilder.executeQuery(uUID.toString(), uUID.toString());
                ArrayList<xyz.refinedev.phoenix.alt.IAltLink> values2 = new ArrayList<>();
                while (sQLBuilder.next()) {
                    IAltLink altLink;
                    if ((altLink = this.findObject(sQLBuilder, AltLink.class)) != null) {
                        values2.add(altLink);
                    }
                }
                values = values2;
            } catch (Throwable throwable) {
                try {
                    sQLBuilder.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            sQLBuilder.close();
            return values;
        } catch (Exception exception) {
            return null;
        }
    }

    private <T> T findObject(SQLBuilder sQLBuilder, Class<T> classValue) throws Exception {
        return (T)
                PlatformGetter.getInstance()
                        .getGson()
                        .fromJson(String.valueOf(sQLBuilder.getObject(1)), classValue);
    }

    private String append(int value) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < value; index++) {
            if (index > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append('?');
        }
        return stringBuilder.toString();
    }

    private boolean matchesEnabled(String text, String text2) throws Exception {
        SQLBuilder sQLBuilder = new SQLBuilder();
        boolean matches;
        try {
            sQLBuilder.setStatement(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?");
            sQLBuilder.executeQuery(text, text2);
            matches = sQLBuilder.next() && ((Number) sQLBuilder.getObject(1)).intValue() > 0;
        } catch (Throwable throwable) {
            try {
                sQLBuilder.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        sQLBuilder.close();
        return matches;
    }

    private MySQLDatabaseHandler getDatabaseHandler() {
        return (MySQLDatabaseHandler) this.platform.getDatabaseHandler();
    }
}
