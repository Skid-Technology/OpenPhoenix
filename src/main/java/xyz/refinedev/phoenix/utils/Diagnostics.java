package xyz.refinedev.phoenix.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.check.PanelCheck;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Diagnostics {

    private static final String SERVER_TEXT = "Server";

    private static final String STORAGE_TEXT = "Storage";

    private static final String NETWORK_TEXT = "Network";

    private static final String CONFLICTS_TEXT = "Conflicts";

    private static final String BEDROCK_TEXT = "Bedrock";

    private static final String MODERN_TEXT = "Modern";

    private Diagnostics() {}

    public static List<SetupCheck> findList(Phoenix phoenix) {
        ArrayList<SetupCheck> values = new ArrayList<>();
        applyPhoenix(phoenix, values);
        applyPhoenixByVoid(phoenix, values);
        applyPhoenixByPhoenix(phoenix, values);
        applyPhoenixByENABLED(phoenix, values);
        applyPhoenixByList(phoenix, values);
        applyPhoenix2(phoenix, values);
        applyPhoenixByV_1_21_6(phoenix, values);
        return values;
    }

    private static void applyPhoenix(Phoenix phoenix, List<SetupCheck> values) {
        try {
            values.add(
                    SetupCheck.of(
                            "Server",
                            "Server version: "
                                    + VersionUtil.getVersion().name()
                                    + (phoenix.getFoliaLib().isFolia() ? " (Folia)" : "")));
            if (phoenix.getPacketEvents() != null && phoenix.getPacketEvents().isLoaded()) {
                values.add(SetupCheck.of("Server", "packetevents is loaded."));
            } else {
                values.add(
                        SetupCheck.ofByFAIL(
                                "Server",
                                "packetevents is not loaded; every modern feature and the nametags are off.",
                                null));
            }
        } catch (Throwable throwable) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Server",
                            "Could not inspect the server: " + throwable.getMessage(),
                            null));
        }
    }

    private static void applyPhoenixByVoid(Phoenix phoenix, List<SetupCheck> values) {
        try {
            long now = System.currentTimeMillis();
            long value = phoenix.getProfileHandler().getRepository().count();
            long value2 = System.currentTimeMillis() - now;
            String text =
                    (phoenix.isMongoDB() ? "MongoDB" : "MySQL")
                            + " answered a count of "
                            + value
                            + " profiles in "
                            + value2
                            + "ms.";
            if (value2 > 1000L) {
                values.add(
                        SetupCheck.ofByWARN(
                                "Storage",
                                text + " That is slow; check the database host and indexes.",
                                null));
            } else {
                values.add(SetupCheck.of("Storage", text));
            }
        } catch (Throwable throwable) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Storage",
                            "The database did not answer: " + throwable.getMessage(),
                            null));
        }
        if (phoenix.getDatabaseHandler().isRedisLocal()) {
            values.add(
                    SetupCheck.of(
                            "Storage", "Redis is off; this server keeps shared state itself."));
        } else {
            try {
                long value3;
                if ((value3 = phoenix.getDatabaseHandler().pingRedisMillis()) < 0L) {
                    values.add(
                            SetupCheck.ofByFAIL("Storage", "Redis did not answer a ping.", null));
                } else if (value3 > 50L) {
                    values.add(
                            SetupCheck.ofByWARN(
                                    "Storage",
                                    "Redis answered in "
                                            + value3
                                            + "ms; cross-server sync will lag behind.",
                                    null));
                } else {
                    values.add(SetupCheck.of("Storage", "Redis answered in " + value3 + "ms."));
                }
            } catch (Throwable throwable2) {
                values.add(
                        SetupCheck.ofByFAIL(
                                "Storage",
                                "Redis did not answer: " + throwable2.getMessage(),
                                null));
            }
        }
    }

    private static void applyPhoenixByPhoenix(Phoenix phoenix, List<SetupCheck> values) {
        try {
            int size2 = phoenix.getNetworkHandler().getProxies().size();
            int size3 = phoenix.getNetworkHandler().getServers().size();
            if (size2 == 0) {
                values.add(
                        SetupCheck.ofByWARN(
                                "Network",
                                "No proxy heartbeat found. Fine for a single server, wrong for a network.",
                                null));
            } else {
                values.add(
                        SetupCheck.of(
                                "Network",
                                size2
                                        + " proxy(ies) and "
                                        + size3
                                        + " server(s) are heartbeating."));
            }
        } catch (Throwable throwable) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Network",
                            "Could not read the network state: " + throwable.getMessage(),
                            null));
        }
    }

    private static void applyPhoenixByENABLED(Phoenix phoenix, List<SetupCheck> values) {
        try {
            if (phoenix.getPanelHandler() == null) {
                return;
            }
            values.addAll(
                    PanelCheck.evaluate(
                            SettingsConfig.PANEL.ENABLED,
                            phoenix.getPanelHandler().create(),
                            System.currentTimeMillis()));
        } catch (Throwable throwable) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Panel",
                            "Could not read the panel link: " + throwable.getMessage(),
                            null));
        }
    }

    private static void applyPhoenixByList(Phoenix phoenix, List<SetupCheck> values) {
        if (Bukkit.getPluginManager().getPlugin("TAB") != null
                && SettingsConfig.MODERN.TABLIST_ORDER.ENABLED) {
            values.add(
                    SetupCheck.ofByWARN(
                            "Conflicts",
                            "TAB is installed while MODERN.TABLIST_ORDER is on; both fight over the list order. Turn one off.",
                            null));
        }
        String[] texts;
        int value = (texts = new String[] {"pxBasic", "pxStaff"}).length;
        for (int index = 0; index < value; index++) {
            String text = texts[index];
            if (Bukkit.getPluginManager().getPlugin(text) != null) {
                values.add(
                        SetupCheck.ofByFAIL(
                                "Conflicts",
                                text
                                        + " is installed but is now built into Phoenix; remove the jar or its commands register twice.",
                                null));
            }
        }
        if (phoenix.getBanWaveHandler() != null) {
            Iterator<IBanWave> iterator2 = phoenix.getBanWaveHandler().pending().iterator();
            while (iterator2.hasNext()) {
                IBanWave banWave;
                if ((banWave = (IBanWave) iterator2.next()).getReleaseAt() > 0L
                        && banWave.getReleaseAt() <= System.currentTimeMillis()) {
                    values.add(
                            SetupCheck.ofByWARN(
                                    "Conflicts",
                                    "The ban wave '"
                                            + banWave.getName()
                                            + "' was due to go out and has not; check redis and the console.",
                                    null));
                }
            }
        }
    }

    private static void applyPhoenix2(Phoenix phoenix, List<SetupCheck> values) {
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            values.add(
                    SetupCheck.of(
                            "Bedrock",
                            "Floodgate is installed; Bedrock players get forms and correct feature gating."));
        } else {
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                if (BedrockUtils.getMostSignificantBits(
                        ((Player) iterator2.next()).getUniqueId())) {
                    values.add(
                            SetupCheck.ofByWARN(
                                    "Bedrock",
                                    "Bedrock-shaped uuids are online but Floodgate is not installed here; Bedrock players will be treated as modern Java clients.",
                                    null));
                    return;
                }
            }
        }
    }

    private static void applyPhoenixByV_1_21_6(Phoenix phoenix, List<SetupCheck> values) {
        if (SettingsConfig.MODERN.DIALOGS.ENABLED
                && !ClientVersionUtil.serverSupports(ServerVersion.V_1_21_6)) {
            values.add(
                    SetupCheck.ofByWARN(
                            "Modern",
                            "Dialogs are on but this server is older than 1.21.6; Java clients fall back to menus. Bedrock players still get forms.",
                            null));
        }
        if (SettingsConfig.MODERN.TRANSFER.REDIRECT_BANNED
                && SettingsConfig.MODERN.TRANSFER.APPEAL_HOST.trim().isEmpty()) {
            values.add(
                    SetupCheck.ofByFAIL(
                            "Modern",
                            "TRANSFER.REDIRECT_BANNED is on but APPEAL_HOST is empty; banned players cannot be redirected anywhere.",
                            null));
        }
        if (SettingsConfig.MODERN.RESOURCE_PACKS.ENABLED) {
            Iterator<String> iterator2 = SettingsConfig.MODERN.RESOURCE_PACKS.PACKS.iterator();
            while (iterator2.hasNext()) {
                String text;
                if ((text = (String) iterator2.next()) == null || text.split("\\|", 4).length < 2) {
                    values.add(
                            SetupCheck.ofByFAIL(
                                    "Modern",
                                    "Resource pack entry '" + text + "' needs at least URL|HASH.",
                                    null));
                }
            }
        }
        if (SettingsConfig.MODERN.REPORT_DETAILS.ENABLED) {
            Iterator<String> iterator3 = SettingsConfig.MODERN.REPORT_DETAILS.DETAILS.iterator();
            while (iterator3.hasNext()) {
                String text2;
                if ((text2 = (String) iterator3.next()) == null || text2.indexOf(124) <= 0) {
                    values.add(
                            SetupCheck.ofByWARN(
                                    "Modern",
                                    "Report detail '"
                                            + text2
                                            + "' is not KEY|VALUE and will be skipped.",
                                    null));
                }
            }
        }
        if (SettingsConfig.MODERN.SERVER_LINKS.ENABLED
                && matchesEnabled(GlobalConfig.NETWORK.WEBSITE)
                && matchesEnabled(GlobalConfig.NETWORK.STORE)
                && matchesEnabled(GlobalConfig.NETWORK.DISCORD)) {
            values.add(
                    SetupCheck.ofByWARN(
                            "Modern",
                            "Server links are on but the network website, store and discord are all empty; most links will resolve to nothing.",
                            null));
        }
        int value;
        if ((value =
                        phoenix.getLocaleHandler() != null
                                ? phoenix.getLocaleHandler().getOverlayCount()
                                : 0)
                > 0) {
            values.add(SetupCheck.of("Modern", value + " locale overlay(s) are loaded."));
        }
    }

    private static boolean matchesEnabled(String text) {
        return text == null || text.trim().isEmpty();
    }
}
