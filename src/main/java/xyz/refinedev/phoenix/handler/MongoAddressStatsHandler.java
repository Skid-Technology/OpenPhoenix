package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.bson.Document;
import xyz.refinedev.lib.mongodb.client.model.UpdateOptions;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.alt.AddressClassification;
import xyz.refinedev.phoenix.alt.AddressStats;
import xyz.refinedev.phoenix.alt.IAddressStats;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.handler.database.MongoDatabaseHandler;
import xyz.refinedev.phoenix.handler.database.MySQLDatabaseHandler;
import xyz.refinedev.phoenix.repository.IAddressStatsRepository;
import xyz.refinedev.phoenix.utils.mysql.SQLBuilder;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MongoAddressStatsHandler implements IAddressStatsHandler {

    private final IAddressStatsRepository repository;

    private final CommonPlatform platform;

    public MongoAddressStatsHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).addressStats();
    }

    public static AddressClassification findAddressClassification(
            IAddressStats addressStats, int value, int value2) {
        AddressClassification addressClassification;
        if ((addressClassification = addressStats.getClassification())
                        != AddressClassification.FORCED_SHARED
                && addressClassification != AddressClassification.FORCED_TRUSTED) {
            if (addressStats.getAccounts() >= value) {
                return AddressClassification.SHARED;
            } else {
                return addressStats.getAccounts() <= value2
                        ? AddressClassification.HOUSEHOLD
                        : AddressClassification.UNKNOWN;
            }
        } else {
            return addressClassification;
        }
    }

    @Override
    public void recordNewAccount(String text, String text2) {
        this.applyString(text);
        if (text2 != null) {
            this.applyString(text2);
        }
    }

    private void applyString(String text) {
        long now = System.currentTimeMillis();
        try {
            if (this.platform.isMongoDB()) {
                this.applyString(text, now);
            } else {
                this.applyStringByRepository(text, now);
            }
            this.applyStringByRepository(text);
        } catch (Exception exception) {
            IAddressStats addressStats;
            if ((addressStats = this.repository.fetchSync(text).orElse(null)) == null) {
                addressStats = new AddressStats(text);
            }
            addressStats.setAccounts(addressStats.getAccounts() + 1);
            addressStats.setLastSeen(now);
            addressStats.setClassification(
                    findAddressClassification(
                            addressStats,
                            GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.AUTO_CLASSIFY_AT,
                            GlobalConfig.SERVER.ALTS.SCORE.HOUSEHOLD_SIZE));
            this.repository.saveSync(text, addressStats);
        }
    }

    private void applyString(String text, long value) {
        Document document =
                new Document("$inc", new Document("accounts", 1))
                        .append("$set", new Document("lastSeen", String.valueOf(value)))
                        .append(
                                "$setOnInsert",
                                new Document("firstSeen", String.valueOf(value))
                                        .append(
                                                "classification",
                                                AddressClassification.UNKNOWN.name())
                                        .append("vpn", false)
                                        .append("mobile", false));
        this.getDatabaseHandler()
                .getAddressStats()
                .updateOne(new Document("_id", text), document, new UpdateOptions().upsert(true));
    }

    private void applyStringByRepository(String text, long value) throws Exception {
        AddressStats addressStats;
        (addressStats = new AddressStats(text)).setAccounts(1);
        addressStats.setLastSeen(value);
        String text2 = this.repository.toJson(addressStats);
        String name = this.getDatabaseHandlerByPlatform().getAddressStats().getName();
        SQLBuilder sQLBuilder = new SQLBuilder();
        try {
            sQLBuilder.setStatement(
                    "INSERT INTO "
                            + name
                            + " (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = JSON_SET(data, '$.accounts', CAST(JSON_UNQUOTE(JSON_EXTRACT(data, '$.accounts')) AS SIGNED) + 1, '$.lastSeen', ?)");
            sQLBuilder.executeUpdate(text, text2, String.valueOf(value));
        } catch (Throwable throwable) {
            try {
                sQLBuilder.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        sQLBuilder.close();
    }

    private void applyStringByRepository(String text) {
        IAddressStats addressStats;
        if ((addressStats = this.repository.fetchSync(text).orElse(null)) != null) {
            AddressClassification addressClassification;
            if ((addressClassification =
                            findAddressClassification(
                                    addressStats,
                                    GlobalConfig.SERVER.ALTS.SHARED_ADDRESS.AUTO_CLASSIFY_AT,
                                    GlobalConfig.SERVER.ALTS.SCORE.HOUSEHOLD_SIZE))
                    != addressStats.getClassification()) {
                addressStats.setClassification(addressClassification);
                this.repository.saveSync(text, addressStats);
            }
        }
    }

    @Override
    public Map<String, IAddressStats> fetch(Set<String> values) {
        if (values.isEmpty()) {
            return new HashMap<>();
        }
        ArrayList values2 = new ArrayList(values);
        HashMap<String, IAddressStats> entries = new HashMap<>();
        for (IAddressStats addressStats :
                this.repository.fetchAllSync(Filters.in("_id", values2))) {
            entries.put(addressStats.getId(), addressStats);
        }
        return entries;
    }

    @Override
    public IAddressStats getOrEmpty(String text) {
        IAddressStats addressStats;
        if ((addressStats = this.repository.fetchSync(text).orElse(null)) != null) {
            return addressStats;
        }
        AddressStats addressStats2;
        (addressStats2 = new AddressStats(text)).setAccounts(1);
        return addressStats2;
    }

    @Override
    public void forceClassification(String text, AddressClassification addressClassification) {
        IAddressStats addressStats;
        if ((addressStats = this.repository.fetchSync(text).orElse(null)) == null) {
            AddressStats addressStats2;
            (addressStats2 = new AddressStats(text)).setAccounts(1);
            addressStats = addressStats2;
        }
        addressStats.setClassification(addressClassification);
        this.repository.saveSync(text, addressStats);
    }

    private MongoDatabaseHandler getDatabaseHandler() {
        return (MongoDatabaseHandler) this.platform.getDatabaseHandler();
    }

    private MySQLDatabaseHandler getDatabaseHandlerByPlatform() {
        return (MySQLDatabaseHandler) this.platform.getDatabaseHandler();
    }

    @Override
    public IAddressStatsRepository getRepository() {
        return this.repository;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }
}
