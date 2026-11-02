package xyz.refinedev.phoenix.alt;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.MongoCollection;
import xyz.refinedev.lib.mongodb.client.MongoCursor;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.MongoAddressStatsHandler;
import xyz.refinedev.phoenix.handler.database.AbstractDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;
import xyz.refinedev.phoenix.utils.mysql.SQLBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class AddressDensityBackfill {

    public static final String text = "__backfill_marker__";

    private final CommonPlatform platform;

    public AddressDensityBackfill(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public void initialize() {
        if (GlobalConfig.SERVER.ALTS.ENABLED) {
            if (GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.BACKFILL_ON_STARTUP) {
                if (!this.platform
                        .getAddressStatsHandler()
                        .getRepository()
                        .fetchSync("__backfill_marker__")
                        .isPresent()) {
                    AddressDensityBackfill addressDensityBackfill = this;
                    CompletableFuture.runAsync(
                            addressDensityBackfill::initializeByPlatform,
                            AbstractDatabaseHandler.DB_EXECUTOR);
                }
            }
        }
    }

    void initializeByPlatform() {
        long now = System.currentTimeMillis();
        this.platform
                .getLogger()
                .info(
                        "(Alts) Backfilling address density from login history. On a large database this runs once and may take a while.");
        try {
            int value = this.platform.isMongoDB() ? this.getLogins() : this.getCount();
            AddressStats addressStats;
            (addressStats = new AddressStats("__backfill_marker__")).setAccounts(value);
            this.platform
                    .getAddressStatsHandler()
                    .getRepository()
                    .saveSync("__backfill_marker__", addressStats);
            this.platform
                    .getLogger()
                    .info(
                            "(Alts) Address density backfill complete: "
                                    + value
                                    + " addresses and subnets in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        } catch (Exception exception) {
            this.platform
                    .getLogger()
                    .log(
                            Level.SEVERE,
                            "(Alts) Address density backfill failed - it will retry on next startup.",
                            exception);
        }
    }

    private int getLogins() {
        MongoCollection logins = this.getDatabaseHandler().getLogins();
        return this.calculateCount(this.findMap(logins, "ip"))
                + this.calculateCount(this.findMap(logins, "subnet"));
    }

    private Map<String, Integer> findMap(MongoCollection<Document> mongoCollection, String text) {
        List<Document> values =
                Arrays.asList(
                        new Document("$match", new Document(text, new Document("$ne", null))),
                        new Document(
                                "$group",
                                new Document(
                                        "_id",
                                        new Document("a", "$" + text).append("t", "$target"))),
                        new Document(
                                "$group",
                                new Document("_id", "$_id.a")
                                        .append("count", new Document("$sum", 1))));
        HashMap<String, Integer> entries = new HashMap<>();
        MongoCursor mongoCursor = mongoCollection.aggregate(values).allowDiskUse(true).iterator();
        while (mongoCursor.hasNext()) {
            Document document;
            Object result;
            if ((result = (document = (Document) mongoCursor.next()).get("_id"))
                    instanceof String) {
                entries.put((String) result, document.getInteger("count", 0));
            }
        }
        return entries;
    }

    private int getCount() throws Exception {
        String name = this.getDatabaseHandlerByPlatform().getLogins().getName();
        return this.calculateCount(this.findMap(name, "ip"))
                + this.calculateCount(this.findMap(name, "subnet"));
    }

    private Map<String, Integer> findMap(String text, String text2) throws Exception {
        HashMap<String, Integer> entries = new HashMap<>();
        SQLBuilder sQLBuilder = new SQLBuilder();
        try {
            sQLBuilder.setStatement(
                    "SELECT JSON_UNQUOTE(JSON_EXTRACT(data, '$."
                            + text2
                            + "')) AS addr, COUNT(DISTINCT JSON_UNQUOTE(JSON_EXTRACT(data, '$.target'))) AS accounts FROM "
                            + text
                            + " GROUP BY addr HAVING addr IS NOT NULL");
            sQLBuilder.executeQuery();
            while (sQLBuilder.next()) {
                Object object = sQLBuilder.getObject(1);
                Object object2 = sQLBuilder.getObject(2);
                if (object instanceof String && object2 instanceof Number) {
                    entries.put((String) object, ((Number) object2).intValue());
                }
            }
        } catch (Throwable throwable) {
            try {
                sQLBuilder.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        sQLBuilder.close();
        return entries;
    }

    private int calculateCount(Map<String, Integer> entries) {
        IAddressStatsRepository repository = this.platform.getAddressStatsHandler().getRepository();
        int value = Math.max(100, GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.BACKFILL_CHUNK_SIZE);
        int value2 = GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.AUTO_CLASSIFY_AT;
        int value3 = GlobalConfig.SERVER.ALTS.SCORE.HOUSEHOLD_SIZE;
        ArrayList values = new ArrayList(entries.keySet());
        int value4 = 0;
        for (int index = 0; index < values.size(); index += value) {
            List<String> values2 = values.subList(index, Math.min(values.size(), index + value));
            Map entries2 = this.platform.getAddressStatsHandler().fetch(new HashSet<>(values2));
            for (String text : values2) {
                IAddressStats addressStats;
                if ((addressStats = (IAddressStats) entries2.get(text)) == null) {
                    addressStats = new AddressStats(text);
                }
                addressStats.setAccounts((Integer) entries.get(text));
                addressStats.setClassification(
                        MongoAddressStatsHandler.findAddressClassification(
                                addressStats, value2, value3));
                repository.saveSync(text, addressStats);
            }
            value4 += values2.size();
            this.platform
                    .getLogger()
                    .info("(Alts) Backfill progress: " + value4 + "/" + values.size());
        }
        return value4;
    }

    private MongoDatabaseHandler getDatabaseHandler() {
        return (MongoDatabaseHandler) this.platform.getDatabaseHandler();
    }

    private MySQLDatabaseHandler getDatabaseHandlerByPlatform() {
        return (MySQLDatabaseHandler) this.platform.getDatabaseHandler();
    }
}
