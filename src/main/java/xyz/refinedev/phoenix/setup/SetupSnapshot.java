package xyz.refinedev.phoenix.setup;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.setup.check.BypassPermissionCatalog;
import xyz.refinedev.phoenix.setup.check.BypassesCheck;
import xyz.refinedev.phoenix.setup.check.ColoursCheck;
import xyz.refinedev.phoenix.setup.check.ConfigMasterCheck;
import xyz.refinedev.phoenix.setup.check.FeaturesCheck;
import xyz.refinedev.phoenix.setup.check.IdentityCheck;
import xyz.refinedev.phoenix.setup.check.RanksCheck;
import xyz.refinedev.phoenix.setup.check.StorageCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SetupSnapshot {

    public static List<SetupCheck> findList(Phoenix phoenix, SetupTier setupTier) {
        return setupTier == SetupTier.FEATURES
                ? SetupRecommendations.addAll(phoenix)
                : addAll(phoenix);
    }

    public static List<SetupCheck> addAll(Phoenix phoenix) {
        ArrayList<SetupCheck> values;
        (values = new ArrayList()).addAll(findList(phoenix));
        values.addAll(findListByNAME(phoenix));
        values.addAll(findListByList(phoenix));
        values.addAll(getList());
        Map<String, Boolean> entries = findMap(phoenix);
        values.addAll(singletonList(entries));
        values.addAll(getListByList());
        values.addAll(findListByPhoenix(phoenix));
        return values;
    }

    private static List<SetupCheck> findList(Phoenix phoenix) {
        long value;
        try {
            value = phoenix.getDatabaseHandler().pingRedisMillis();
        } catch (Throwable throwable) {
            value = -1L;
        }
        long value2;
        long value3;
        try {
            long now = System.currentTimeMillis();
            value3 = phoenix.getProfileHandler().getRepository().count();
            value2 = System.currentTimeMillis() - now;
        } catch (Throwable throwable2) {
            value2 = -1L;
            value3 = 0L;
        }
        return StorageCheck.evaluate(
                value,
                value2,
                value3,
                phoenix.isMongoDB(),
                phoenix.getDatabaseHandler().isRedisLocal());
    }

    private static List<SetupCheck> findListByNAME(Phoenix phoenix) {
        try {
            Set<IServerData> servers = phoenix.getNetworkHandler().getServers();
            boolean matches = false;
            for (IServerData serverData : servers) {
                if (GlobalConfig.SERVER.NAME.equals(serverData.getServerName())) {
                    matches = true;
                    break;
                }
            }
            return IdentityCheck.evaluate(
                    GlobalConfig.SERVER.NAME,
                    GlobalConfig.SERVER.DISPLAYNAME,
                    matches,
                    servers.size());
        } catch (Throwable throwable) {
            return Collections.singletonList(
                    SetupCheck.ofByFAIL(
                            "Identity",
                            "Could not read the network state: " + throwable.getMessage(),
                            null));
        }
    }

    private static List<SetupCheck> findListByList(Phoenix phoenix) {
        try {
            List<IRank> cache = phoenix.getRankHandler().getRepository().getCache();
            int value = 0;
            Iterator iterator2 = cache.iterator();
            while (iterator2.hasNext()) {
                if (((IRank) iterator2.next()).isStaff()) {
                    value++;
                }
            }
            return RanksCheck.evaluate(cache.size(), value);
        } catch (Throwable throwable) {
            return Collections.singletonList(
                    SetupCheck.ofByFAIL(
                            "Ranks", "Could not read the ranks: " + throwable.getMessage(), null));
        }
    }

    private static List<SetupCheck> getList() {
        return ColoursCheck.evaluate(
                GlobalConfig.NETWORK.COLORS.MAIN,
                GlobalConfig.NETWORK.COLORS.SECONDARY,
                GlobalConfig.NETWORK.COLORS.MIDDLE);
    }

    private static Map<String, Boolean> findMap(Phoenix phoenix) {
        try {
            LinkedHashMap<String, Boolean> entries = new LinkedHashMap<>();
            for (String text : BypassPermissionCatalog.strings) {
                boolean matches = false;
                Iterator<IRank> iterator2 =
                        phoenix.getRankHandler().getRepository().getCache().iterator();
                while (iterator2.hasNext()) {
                    if (((IRank) iterator2.next()).hasPermission(text)) {
                        matches = true;
                        break;
                    }
                }
                entries.put(text, matches);
            }
            return entries;
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static List<SetupCheck> singletonList(Map<String, Boolean> entries) {
        return entries == null
                ? Collections.singletonList(
                        SetupCheck.ofByFAIL(
                                "Bypasses", "Could not read the rank permissions.", null))
                : BypassesCheck.evaluate(entries, getMap());
    }

    private static Map<String, Boolean> getMap() {
        LinkedHashMap<String, Boolean> entries;
        (entries = new LinkedHashMap()).put("chat-control", SettingsConfig.CHAT_CONTROL.ENABLED);
        entries.put("filter", GlobalConfig.FILTER.ENABLED);
        entries.put("cooldowns", true);
        entries.put("antispam", true);
        return entries;
    }

    private static List<SetupCheck> getListByList() {
        return FeaturesCheck.evaluate(FeatureToggles.getMap(), BypassPermissionCatalog.getList());
    }

    private static List<SetupCheck> findListByPhoenix(Phoenix phoenix) {
        try {
            String text;
            boolean matches =
                    (text =
                                            phoenix.getDatabaseHandler()
                                                    .runRedisCommand(
                                                            result ->
                                                                    (String)
                                                                            result.get(
                                                                                    "px-configs-master")))
                                    != null
                            && phoenix.getNetworkHandler().isServerOnline(text);
            return ConfigMasterCheck.evaluate(
                    text,
                    GlobalConfig.SERVER.NAME,
                    matches,
                    phoenix.getNetworkHandler().getServers().size());
        } catch (Throwable throwable) {
            return Collections.singletonList(
                    SetupCheck.ofByFAIL(
                            "Config master",
                            "Could not read the config master: " + throwable.getMessage(),
                            null));
        }
    }

    private SetupSnapshot() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
