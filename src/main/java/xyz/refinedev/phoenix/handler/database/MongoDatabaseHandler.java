package xyz.refinedev.phoenix.handler.database;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.bson.conversions.Bson;
import xyz.refinedev.lib.mongodb.ConnectionString;
import xyz.refinedev.lib.mongodb.MongoClientSettings;
import xyz.refinedev.lib.mongodb.MongoClientSettings.Builder;
import xyz.refinedev.lib.mongodb.client.MongoClient;
import xyz.refinedev.lib.mongodb.client.MongoClients;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.lib.mongodb.client.MongoDatabase;
import xyz.refinedev.lib.mongodb.client.model.Indexes;
import xyz.refinedev.lib.mongodb.event.ConnectionCheckOutFailedEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionCheckOutStartedEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionCheckedInEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionCheckedOutEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionClosedEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionCreatedEvent;
import xyz.refinedev.lib.mongodb.event.ConnectionPoolListener;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDatabaseHandler extends AbstractDatabaseHandler {

    private MongoClient client;

    private MongoDatabase database;

    private MongoCollection<Document> profiles;

    private MongoCollection<Document> grants;

    private MongoCollection<Document> punishments;

    private MongoCollection<Document> ranks;

    private MongoCollection<Document> logins;

    private MongoCollection<Document> serverHops;

    private MongoCollection<Document> commandLogs;

    private MongoCollection<Document> notifications;

    private MongoCollection<Document> filters;

    private MongoCollection<Document> tags;

    private MongoCollection<Document> tagCategories;

    private MongoCollection<Document> chatSnapshots;

    private MongoCollection<Document> chatLogs;

    private MongoCollection<Document> disguiseHistory;

    private MongoCollection<Document> punishmentLadders;

    private MongoCollection<Document> security;

    private MongoCollection<Document> customTags;

    private MongoCollection<Document> altLinks;

    private MongoCollection<Document> addressStats;

    private MongoCollection<Document> reports;

    private MongoCollection<Document> reportCategories;

    private final AtomicLong poolCreated = new AtomicLong();

    private final AtomicLong poolClosed = new AtomicLong();

    private final AtomicLong poolCheckedOut = new AtomicLong();

    private final AtomicLong poolCheckedIn = new AtomicLong();

    private final AtomicLong poolPending = new AtomicLong();

    public MongoDatabaseHandler(CommonPlatform commonPlatform) {
        super(commonPlatform);
    }

    public static MongoDatabaseHandler orNull(IDatabaseHandler databaseHandler) {
        return databaseHandler instanceof MongoDatabaseHandler
                ? (MongoDatabaseHandler) databaseHandler
                : null;
    }

    @Override
    public boolean enable() {
        return this.setupMongo() && this.setupRedis();
    }

    public boolean setupMongo() {
        Logger.getLogger("org.mongodb").setLevel(Level.SEVERE);
        try {
            String text =
                    DatabaseSettings.formatString(
                            GlobalConfig.DATABASE.MONGO.URI_MODE,
                            GlobalConfig.DATABASE.MONGO.MONGO_URI,
                            GlobalConfig.DATABASE.MONGO.AUTHENTICATION,
                            GlobalConfig.DATABASE.MONGO.USERNAME,
                            GlobalConfig.DATABASE.MONGO.PASSWORD,
                            GlobalConfig.DATABASE.MONGO.ADDRESS,
                            GlobalConfig.DATABASE.MONGO.PORT);
            Builder builder2 =
                    MongoClientSettings.builder().applyConnectionString(new ConnectionString(text));
            MongoClientSettings mongoClientSettings =
                    builder2.applyToConnectionPoolSettings(
                                    result ->
                                            result.addConnectionPoolListener(
                                                    this.buildPoolListener()))
                            .build();
            this.client = MongoClients.create(mongoClientSettings);
            this.database = this.client.getDatabase(GlobalConfig.DATABASE.MONGO.DATABASE);
            this.profiles = this.database.getCollection("px-profiles");
            this.ranks = this.database.getCollection("px-ranks");
            this.grants = this.database.getCollection("px-grants");
            this.logins = this.database.getCollection("px-logins");
            this.serverHops = this.database.getCollection("px-serverHops");
            this.punishments = this.database.getCollection("punishments");
            this.filters = this.database.getCollection("px-filters");
            this.commandLogs = this.database.getCollection("commandLogs");
            this.notifications = this.database.getCollection("notifications");
            this.tags = this.database.getCollection("tags");
            this.chatLogs = this.database.getCollection("px-chatLogs");
            this.tagCategories = this.database.getCollection("tagcategories");
            this.disguiseHistory = this.database.getCollection("px-disguiseHistory");
            this.chatSnapshots = this.database.getCollection("px-chatSnapshots");
            this.punishmentLadders = this.database.getCollection("px-punishmentLadders");
            this.security = this.database.getCollection("px-security");
            this.customTags = this.database.getCollection("px-customTags");
            this.altLinks = this.database.getCollection("px-altLinks");
            this.addressStats = this.database.getCollection("px-addressStats");
            this.reports = this.database.getCollection("px-reports");
            this.reportCategories = this.database.getCollection("px-report-categories");
            this.setupIndexes();
            super.platform.getLogger().info("Connected to MongoDB successfully!");
            return true;
        } catch (Exception exception) {
            this.logConnectFailure("mongo", exception);
            return false;
        }
    }

    public void setupIndexes() {
        this.createIndexes(this.chatSnapshots, "requestedBy", "niceId");
        this.createIndexes(this.commandLogs, "issuedBy", "server");
        this.createIndexes(this.grants, "issuedBy", "removedBy", "target", "rankId");
        this.createIndexes(this.logins, "target", "ip");
        this.logins.createIndex(
                Indexes.compoundIndex(
                        new Bson[] {
                            Indexes.ascending(new String[] {"ip"}),
                            Indexes.descending(new String[] {"login"})
                        }));
        this.createIndexes(this.altLinks, "a", "b");
        this.createIndexes(this.serverHops, "target");
        this.createIndexes(this.profiles, "target", "nameToLowercase", "currentAddress", "alts");
        this.createIndexes(
                this.punishments, "issuedBy", "removedBy", "target", "punishmentID", "address");
        this.createIndexes(this.chatLogs, "sender");
        this.punishments.createIndex(Indexes.descending(new String[] {"issuedAt"}));
        String[] texts = new String[6];
        texts[0] = "punishmentType";
        texts[1] = "target";
        texts[2] = "issuedBy";
        texts[3] = "issuedOn";
        texts[4] = "active";
        texts[5] = "voided";
        String[] texts2 = texts;
        int value = texts.length;
        for (int index = 0; index < value; index++) {
            String text = texts2[index];
            this.punishments.createIndex(
                    Indexes.compoundIndex(
                            new Bson[] {
                                Indexes.ascending(new String[] {text}),
                                Indexes.descending(new String[] {"issuedAt"})
                            }));
        }
    }

    @Override
    public void disable() {
        super.disable();
        this.client.close();
    }

    @Override
    public Map<String, Long> getPoolStats() {
        long value = Math.max(0L, this.poolCreated.get() - this.poolClosed.get());
        long value2 = Math.max(0L, this.poolCheckedOut.get() - this.poolCheckedIn.get());
        HashMap<String, Long> entries;
        (entries = new HashMap()).put("total", value);
        entries.put("active", value2);
        entries.put("idle", Math.max(0L, value - value2));
        entries.put("awaiting", Math.max(0L, this.poolPending.get()));
        return entries;
    }

    private ConnectionPoolListener buildPoolListener() {
        return new ConnectionPoolListener() {

            public void connectionCreated(ConnectionCreatedEvent result) {
                MongoDatabaseHandler.this.poolCreated.incrementAndGet();
            }

            public void connectionClosed(ConnectionClosedEvent result) {
                MongoDatabaseHandler.this.poolClosed.incrementAndGet();
            }

            public void connectionCheckOutStarted(ConnectionCheckOutStartedEvent result) {
                MongoDatabaseHandler.this.poolPending.incrementAndGet();
            }

            public void connectionCheckedOut(ConnectionCheckedOutEvent result) {
                MongoDatabaseHandler.this.poolCheckedOut.incrementAndGet();
                MongoDatabaseHandler.this.poolPending.decrementAndGet();
            }

            public void connectionCheckOutFailed(ConnectionCheckOutFailedEvent result) {
                MongoDatabaseHandler.this.poolPending.decrementAndGet();
            }

            public void connectionCheckedIn(ConnectionCheckedInEvent result) {
                MongoDatabaseHandler.this.poolCheckedIn.incrementAndGet();
            }
        };
    }

    public void createIndexes(MongoCollection<Document> mongoCollection, String... text) {
        List values =
                (List)
                        mongoCollection
                                .listIndexes()
                                .map(result -> result.getString("name"))
                                .into(new ArrayList());
        String[] texts = text;
        int value = text.length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            if (!values.contains(text2)) {
                mongoCollection.createIndex(Indexes.ascending(new String[] {text2}));
            }
        }
    }

    public MongoClient getClient() {
        return this.client;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getProfiles() {
        return this.profiles;
    }

    public MongoCollection<Document> getGrants() {
        return this.grants;
    }

    public MongoCollection<Document> getPunishments() {
        return this.punishments;
    }

    public MongoCollection<Document> getRanks() {
        return this.ranks;
    }

    public MongoCollection<Document> getLogins() {
        return this.logins;
    }

    public MongoCollection<Document> getServerHops() {
        return this.serverHops;
    }

    public MongoCollection<Document> getCommandLogs() {
        return this.commandLogs;
    }

    public MongoCollection<Document> getNotifications() {
        return this.notifications;
    }

    public MongoCollection<Document> getFilters() {
        return this.filters;
    }

    public MongoCollection<Document> getTags() {
        return this.tags;
    }

    public MongoCollection<Document> getTagCategories() {
        return this.tagCategories;
    }

    public MongoCollection<Document> getChatSnapshots() {
        return this.chatSnapshots;
    }

    public MongoCollection<Document> getChatLogs() {
        return this.chatLogs;
    }

    public MongoCollection<Document> getDisguiseHistory() {
        return this.disguiseHistory;
    }

    public MongoCollection<Document> getPunishmentLadders() {
        return this.punishmentLadders;
    }

    public MongoCollection<Document> getSecurity() {
        return this.security;
    }

    public MongoCollection<Document> getCustomTags() {
        return this.customTags;
    }

    public MongoCollection<Document> getAltLinks() {
        return this.altLinks;
    }

    public MongoCollection<Document> getAddressStats() {
        return this.addressStats;
    }

    public MongoCollection<Document> getReports() {
        return this.reports;
    }

    public MongoCollection<Document> getReportCategories() {
        return this.reportCategories;
    }

    public AtomicLong getPoolCreated() {
        return this.poolCreated;
    }

    public AtomicLong getPoolClosed() {
        return this.poolClosed;
    }

    public AtomicLong getPoolCheckedOut() {
        return this.poolCheckedOut;
    }

    public AtomicLong getPoolCheckedIn() {
        return this.poolCheckedIn;
    }

    public AtomicLong getPoolPending() {
        return this.poolPending;
    }
}
