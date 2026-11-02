package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.lib.idb.DB;
import xyz.refinedev.lib.idb.Database;
import xyz.refinedev.lib.idb.DatabaseOptions;
import xyz.refinedev.lib.idb.HikariPooledDatabase;
import xyz.refinedev.lib.idb.PooledDatabaseOptions;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.utils.mysql.Attribute;
import xyz.refinedev.phoenix.utils.mysql.JSONColumn;
import xyz.refinedev.phoenix.utils.mysql.Key;
import xyz.refinedev.phoenix.utils.mysql.StringColumn;
import xyz.refinedev.phoenix.utils.mysql.Table;
import xyz.refinedev.phoenix.utils.mysql.UUIDColumn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MySQLDatabaseHandler extends AbstractDatabaseHandler {

    private Database database;

    private Table profiles;

    private Table grants;

    private Table punishments;

    private Table ranks;

    private Table logins;

    private Table serverHops;

    private Table commandLogs;

    private Table notifications;

    private Table filters;

    private Table tags;

    private Table tagCategories;

    private Table chatSnapshots;

    private Table chatLogs;

    private Table disguiseHistory;

    private Table punishmentLadders;

    private Table security;

    private Table customTags;

    private Table altLinks;

    private Table addressStats;

    private Table reports;

    private Table reportCategories;

    public MySQLDatabaseHandler(CommonPlatform commonPlatform) {
        super(commonPlatform);
    }

    @Override
    public boolean enable() {
        return this.setupMySQL() && this.setupRedis();
    }

    public boolean setupMySQL() {
        try {
            DatabaseOptions databaseOptions =
                    DatabaseOptions.builder()
                            .driverClassName("xyz.refinedev.lib.com.mysql.cj.jdbc.Driver")
                            .mysql(
                                    GlobalConfig.DATABASE.MYSQL.USERNAME,
                                    GlobalConfig.DATABASE.MYSQL.PASSWORD,
                                    GlobalConfig.DATABASE.MYSQL.DATABASE,
                                    GlobalConfig.DATABASE.MYSQL.ADDRESS
                                            + ":"
                                            + GlobalConfig.DATABASE.MYSQL.PORT)
                            .build();
            HikariPooledDatabase hikariPooledDatabase;
            DB.setGlobalDatabase(
                    hikariPooledDatabase =
                            PooledDatabaseOptions.builder()
                                    .options(databaseOptions)
                                    .createHikariDatabase());
            this.database = hikariPooledDatabase;
            this.profiles = this.createJsonTable("pxprofiles");
            this.ranks = this.createJsonTable("pxranks");
            this.logins = this.createJsonTable("pxlogins");
            this.serverHops = this.createJsonTable("pxserverHops");
            this.grants = this.createJsonTable("pxgrants");
            this.punishments = this.createJsonTable("pxpunishments");
            this.filters = this.createStringTable("pxfilters", 255);
            this.commandLogs = this.createJsonTable("pxcommandLogs");
            this.notifications = this.createStringTable("pxnotifications", 255);
            this.tags = this.createStringTable("pxtags", 255);
            this.chatLogs = this.createStringTable("pxchatLogs", 8);
            this.tagCategories = this.createStringTable("pxtagcategories", 255);
            this.disguiseHistory = this.createJsonTable("pxdisguiseHistory");
            this.chatSnapshots = this.createJsonTable("pxchatSnapshots");
            this.punishmentLadders = this.createStringTable("pxpunishmentLadders", 255);
            this.security = this.createJsonTable("pxsecurity");
            this.customTags = this.createStringTable("pxcustomTags", 255);
            this.altLinks = this.createStringTable("pxaltLinks", 96);
            this.addressStats = this.createStringTable("pxaddressStats", 255);
            this.reports = this.createJsonTable("pxreports");
            this.reportCategories = this.createStringTable("pxReportCategories", 255);
            super.platform.getLogger().info("Connected to MySQL successfully!");
            return true;
        } catch (Exception exception) {
            this.logConnectFailure("mysql", exception);
            return false;
        }
    }

    @Override
    public void disable() {
        super.disable();
        this.database.close();
    }

    @Override
    public Map<String, Long> getPoolStats() {
        if (this.database == null) {
            return Collections.emptyMap();
        }
        try {
            Object result;
            Object result2 =
                    (result =
                                    this.database
                                            .getClass()
                                            .getMethod("getDataSource")
                                            .invoke(this.database))
                            .getClass()
                            .getMethod("getHikariPoolMXBean")
                            .invoke(result);
            HashMap<String, Long> entries;
            (entries = new HashMap())
                    .put("active", this.invokeLong(result2, "getActiveConnections"));
            entries.put("idle", this.invokeLong(result2, "getIdleConnections"));
            entries.put("total", this.invokeLong(result2, "getTotalConnections"));
            entries.put("awaiting", this.invokeLong(result2, "getThreadsAwaitingConnection"));
            return entries;
        } catch (Throwable throwable) {
            return Collections.emptyMap();
        }
    }

    private long invokeLong(Object result, String text) throws ReflectiveOperationException {
        return ((Number) result.getClass().getMethod(text).invoke(result)).longValue();
    }

    private Table createJsonTable(final String text) {
        return new Table() {

            @Override
            public String getName() {
                return text;
            }

            @Override
            public Attribute[] getAttributes() {
                return new Attribute[] {
                    Key.primary(UUIDColumn.of("id")), UUIDColumn.of("id"), JSONColumn.of("data")
                };
            }
        };
    }

    private Table createStringTable(final String text, final int value) {
        return new Table() {

            @Override
            public String getName() {
                return text;
            }

            @Override
            public Attribute[] getAttributes() {
                return new Attribute[] {
                    Key.primary(StringColumn.of("id", value)),
                    StringColumn.of("id", value),
                    JSONColumn.of("data")
                };
            }
        };
    }

    public Database getDatabase() {
        return this.database;
    }

    public Table getProfiles() {
        return this.profiles;
    }

    public Table getGrants() {
        return this.grants;
    }

    public Table getPunishments() {
        return this.punishments;
    }

    public Table getRanks() {
        return this.ranks;
    }

    public Table getLogins() {
        return this.logins;
    }

    public Table getServerHops() {
        return this.serverHops;
    }

    public Table getCommandLogs() {
        return this.commandLogs;
    }

    public Table getNotifications() {
        return this.notifications;
    }

    public Table getFilters() {
        return this.filters;
    }

    public Table getTags() {
        return this.tags;
    }

    public Table getTagCategories() {
        return this.tagCategories;
    }

    public Table getChatSnapshots() {
        return this.chatSnapshots;
    }

    public Table getChatLogs() {
        return this.chatLogs;
    }

    public Table getDisguiseHistory() {
        return this.disguiseHistory;
    }

    public Table getPunishmentLadders() {
        return this.punishmentLadders;
    }

    public Table getSecurity() {
        return this.security;
    }

    public Table getCustomTags() {
        return this.customTags;
    }

    public Table getAltLinks() {
        return this.altLinks;
    }

    public Table getAddressStats() {
        return this.addressStats;
    }

    public Table getReports() {
        return this.reports;
    }

    public Table getReportCategories() {
        return this.reportCategories;
    }
}
