package xyz.refinedev.phoenix.handler;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.antivpn.api.IAntiVPNProvider;
import xyz.refinedev.phoenix.antivpn.api.impl.AntiVPNProvider;
import xyz.refinedev.phoenix.antivpn.provider.IpRiskProvider;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.profile.AntiVPNWhitelistUpdatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.utils.JsonTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class RedisAntiVPNHandler implements IAntiVPNHandler {

    private final CommonPlatform platform;

    private final Map<String, IAntiVPNData> antiVPNDatasByName = new HashMap<>();

    private final Set<UUID> antiVpnBypass = new HashSet<>();

    private final IAntiVPNProvider antiVPNProvider;

    public RedisAntiVPNHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        switch (GlobalConfig.ANTIVPN.PROVIDER.toLowerCase()) {
            case "iprisk":
                this.antiVPNProvider = new IpRiskProvider(commonPlatform);
                break;
            case "proxycheck":
                this.antiVPNProvider =
                        new AntiVPNProvider(
                                commonPlatform, GlobalConfig.ANTIVPN.PROXYCHECK_API_KEY);
                break;
            default:
                this.antiVPNProvider = null;
        }
    }

    @Override
    public void fetchAntiVpnBypass() {
        this.antiVpnBypass.clear();
        String text;
        if ((text =
                        this.platform
                                .getDatabaseHandler()
                                .runRedisCommand(
                                        result -> (String) result.get("antivpn-whitelist")))
                != null) {
            this.antiVpnBypass.addAll(
                    (Collection<? extends UUID>)
                            this.platform.getGson().fromJson(text, JsonTypes.type));
        }
    }

    @Override
    public void whitelist(UUID uUID, boolean matches) {
        this.antiVpnBypass.add(uUID);
        if (matches) {
            this.platform.getPacketHandler().send(new AntiVPNWhitelistUpdatePacket(uUID, true));
            IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result ->
                            result.set(
                                    "antivpn-whitelist",
                                    this.platform
                                            .getGson()
                                            .toJson(this.antiVpnBypass, JsonTypes.type)));
        }
    }

    @Override
    public void unwhitelist(UUID uUID, boolean matches) {
        this.antiVpnBypass.remove(uUID);
        if (matches) {
            this.platform.getPacketHandler().send(new AntiVPNWhitelistUpdatePacket(uUID, false));
            IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result ->
                            result.set(
                                    "antivpn-whitelist",
                                    this.platform
                                            .getGson()
                                            .toJson(this.antiVpnBypass, JsonTypes.type)));
        }
    }

    @Override
    public void cacheVpnDataRedis(IAntiVPNData antiVPNData) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result ->
                        result.hset(
                                "antivpndata:" + antiVPNData.getUuid(),
                                antiVPNData.getHashedIp(),
                                this.platform.getGson().toJson(antiVPNData)));
    }

    @Override
    public Map<UUID, IAntiVPNData> fetchLatestVpnDataRedis(Set<IProfile> values) {
        HashMap<UUID, IAntiVPNData> entries = new HashMap<>();
        for (IProfile profile : values) {
            IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
            String text;
            if ((text =
                            databaseHandler.runRedisCommand(
                                    result ->
                                            (String)
                                                    result.hget(
                                                            "antivpndata:" + profile.getUniqueId(),
                                                            profile.getCurrentAddress())))
                    != null) {
                entries.put(
                        profile.getUniqueId(),
                        (IAntiVPNData) this.platform.getGson().fromJson(text, IAntiVPNData.class));
            }
        }
        return entries;
    }

    @Override
    public IAntiVPNData fetchLatestVpnDataRedis(UUID uUID, String text) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        String text2;
        return (text2 =
                                databaseHandler.runRedisCommand(
                                        result ->
                                                (String) result.hget("antivpndata:" + uUID, text)))
                        == null
                ? null
                : (IAntiVPNData) this.platform.getGson().fromJson(text2, IAntiVPNData.class);
    }

    @Override
    public List<IAntiVPNData> fetchVpnDataRedis(UUID uUID) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        Map<String, String> entries =
                databaseHandler.runRedisCommand(result -> result.hgetall("antivpndata:" + uUID));
        ArrayList<IAntiVPNData> values2 = new ArrayList<>();
        if (entries == null) {
            return values2;
        }
        for (String text : entries.values()) {
            IAntiVPNData antiVPNData =
                    (IAntiVPNData) this.platform.getGson().fromJson(text, IAntiVPNData.class);
            values2.add(antiVPNData);
        }
        return values2;
    }

    @Override
    public void removeVpnDataRedis(IAntiVPNData antiVPNData) {
        IDatabaseHandler databaseHandler = this.platform.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result ->
                        result.hdel(
                                "antivpndata:" + antiVPNData.getUuid(),
                                new String[] {antiVPNData.getHashedIp()}));
    }

    @Override
    public boolean isEnabled() {
        return this.antiVPNProvider != null;
    }

    @Override
    public boolean hasBypass(UUID uUID) {
        return this.antiVpnBypass.contains(uUID);
    }

    @Override
    public boolean check(UUID uUID, String text) {
        if (this.antiVPNProvider == null) {
            return false;
        }
        boolean matches = this.hasBypass(uUID);
        String text2 = this.platform.getHashHandler().hash(text);
        if (GlobalConfig.ANTIVPN.BACKGROUND_CHECK) {
            if (this.getAntiVPNData(text2) != null) {
                return false;
            }
            CompletableFuture<Void> completableFuture =
                    CompletableFuture.runAsync(
                            () -> {
                                IAntiVPNData antiVPNData;
                                if ((antiVPNData = this.antiVPNProvider.check(uUID, text))
                                        == null) {
                                    this.platform
                                            .getLogger()
                                            .log(
                                                    Level.SEVERE,
                                                    "Request to fetch VPN Data for "
                                                            + uUID
                                                            + " failed.");
                                } else {
                                    this.antiVPNDatasByName.put(text2, antiVPNData);
                                    this.cacheVpnDataRedis(antiVPNData);
                                    if (antiVPNData.isVpn()) {
                                        this.applyString("background", matches);
                                    }
                                }
                            });
            completableFuture.whenComplete(
                    (result, result2) -> {
                        if (result2 != null) {
                            this.platform
                                    .getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "There was an error while fetching VPN Data for: "
                                                    + uUID,
                                            result2);
                        }
                    });
            return false;
        } else {
            IAntiVPNData antiVPNData2;
            if ((antiVPNData2 = this.getAntiVPNData(text2)) != null) {
                boolean matches2;
                if (matches2 = antiVPNData2.isVpn()) {
                    this.applyString("sync_cached", matches);
                }
                return matches2 && !matches;
            } else {
                IAntiVPNData antiVPNData3;
                if ((antiVPNData3 = this.antiVPNProvider.check(uUID, text)) == null) {
                    return false;
                }
                this.antiVPNDatasByName.put(text2, antiVPNData3);
                this.cacheVpnDataRedis(antiVPNData3);
                if (antiVPNData3.isVpn()) {
                    this.applyString("sync_fresh", matches);
                }
                return antiVPNData3.isVpn() && !matches;
            }
        }
    }

    private void applyString(String text, boolean matches) {
        this.platform
                .getMetricsHandler()
                .metric("phoenix_antivpn_detection")
                .findMetric("mode", text)
                .findMetric("bypassed", String.valueOf(matches))
                .initialize();
    }

    @Override
    public IAntiVPNData getAntiVPNData(UUID uUID) {
        Iterator<IAntiVPNData> iterator2 = this.antiVPNDatasByName.values().iterator();
        while (iterator2.hasNext()) {
            IAntiVPNData antiVPNData;
            if ((antiVPNData = (IAntiVPNData) iterator2.next()).getUuid().equals(uUID)) {
                return antiVPNData;
            }
        }
        return null;
    }

    @Override
    public IAntiVPNData getAntiVPNData(String text) {
        return this.antiVPNDatasByName.get(text);
    }

    public RedisAntiVPNHandler(CommonPlatform commonPlatform, IAntiVPNProvider antiVPNProvider2) {
        this.platform = commonPlatform;
        this.antiVPNProvider = antiVPNProvider2;
    }

    @Override
    public Set<UUID> getAntiVpnBypass() {
        return this.antiVpnBypass;
    }
}
