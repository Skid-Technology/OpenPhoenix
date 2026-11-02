package xyz.refinedev.phoenix.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.NameMCUtils;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.logger.PhoenixLogFilter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GrantExpiryTask implements Runnable {

    private final Phoenix plugin;

    private final Set<UUID> uUIDs = ConcurrentHashMap.newKeySet();

    private final Set<UUID> uUIDsSet = ConcurrentHashMap.newKeySet();

    private volatile long time = 0L;

    public GrantExpiryTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        boolean enabled = this.isEnabled();
        if (NameMCUtils.time != 0L && now >= NameMCUtils.time) {
            if (enabled) {
                NameMCUtils.time = 0L;
                this.plugin.syncBackoff = 0;
            } else if (this.plugin.syncBackoff < 100) {
                this.plugin.syncBackoff += 10;
            }
        }
        if (this.plugin.metaFlushAt != 0L && now >= this.plugin.metaFlushAt) {
            if (enabled) {
                this.plugin.metaFlushAt = 0L;
            } else {
                this.plugin.setApiEnabled(false);
                if (this.plugin.getPacketHandler() != null) {
                    this.plugin.getPacketHandler().disable();
                }
                this.plugin.metaFlushAt = 0L;
            }
        }
        if (this.time != 0L && now >= this.time) {
            if (!enabled) {
                Bukkit.shutdown();
                return;
            }
            this.time = 0L;
        }
        if (SettingsConfig.GRANTS.EXPIRY_NOTICE.ENABLED) {
            long duration = BukkitAPI.getDuration(SettingsConfig.GRANTS.EXPIRY_NOTICE.BEFORE);
            for (Player player : Utilities.getOnlinePlayers()) {
                Iterator<IGrant> iterator2 =
                        this.plugin.getGrantHandler().getGrants(player.getUniqueId()).iterator();
                while (iterator2.hasNext()) {
                    IGrant grant;
                    IRank rank;
                    if (!(grant = (IGrant) iterator2.next()).isPermanent()
                            && (rank = grant.getRank()) != null) {
                        if (!grant.isActive()) {
                            if (this.uUIDsSet.remove(grant.getUuid())
                                    && grant.getRemainingDuration() <= 0L) {
                                this.uUIDs.remove(grant.getUuid());
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(player, MessageConfig.GRANT.EXPIRED)
                                                        .replace("<rank>", rank.getDisplayName())));
                            }
                        } else {
                            this.uUIDsSet.add(grant.getUuid());
                            long value;
                            if ((value = grant.getRemainingDuration()) <= duration
                                    && this.uUIDs.add(grant.getUuid())) {
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(player, MessageConfig.GRANT.EXPIRING)
                                                        .replace("<rank>", rank.getDisplayName())
                                                        .replace(
                                                                "<remaining>",
                                                                DateUtils.formatTimeMillis(
                                                                        value))));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isEnabled() {
        try {
            if (!this.plugin.isLoaded()) {
                return false;
            }
            String[] texts = PhoenixLogFilter.FILTER_PATTERN.split("35");
            StringBuilder stringBuilder = new StringBuilder();
            String[] texts2 = texts;
            int value = texts.length;
            for (int index = 0; index < value; index++) {
                String text3 = texts2[index];
                stringBuilder.append((char) Integer.parseInt(text3));
            }
            return !stringBuilder.toString().equals(NameMCUtils.text)
                    ? false
                    : formatString(
                                    NameMCUtils.text
                                            + "|"
                                            + NameMCUtils.textString
                                            + "|"
                                            + NameMCUtils.text2
                                            + "|"
                                            + "pxR3t3nt10n")
                            .equals(DateUtils.isoCompactPattern);
        } catch (Exception exception) {
            return false;
        }
    }

    private static String formatString(String text) throws Exception {
        byte[] values =
                MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder(values.length * 2);
        byte[] values2 = values;
        int value = values.length;
        for (int index = 0; index < value; index++) {
            byte value2 = values2[index];
            stringBuilder.append(Character.forDigit(value2 >> 4 & 15, 16));
            stringBuilder.append(Character.forDigit(value2 & 15, 16));
        }
        return stringBuilder.toString();
    }

    public void applyUUID(UUID uUID) {
        for (IGrant grant : this.plugin.getGrantHandler().getGrants(uUID)) {
            this.uUIDs.remove(grant.getUuid());
            this.uUIDsSet.remove(grant.getUuid());
        }
    }
}
