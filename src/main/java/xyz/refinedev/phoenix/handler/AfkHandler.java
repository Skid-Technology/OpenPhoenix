package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AfkHandler {

    private static final long CHECK_INTERVAL_TICKS = 100L;

    private final Phoenix plugin;

    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();

    private final Map<UUID, Boolean> afk = new ConcurrentHashMap<>();

    private static final String PERMISSION = "core.staff";

    public AfkHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        Tasks.runAsyncTimer(this::check, 100L, 100L);
    }

    public void recordActivity(UUID uUID) {
        this.lastActivity.put(uUID, System.currentTimeMillis());
        this.afk.remove(uUID);
    }

    public void forget(UUID uUID) {
        this.lastActivity.remove(uUID);
        this.afk.remove(uUID);
    }

    public boolean isAfk(UUID uUID) {
        return this.afk.getOrDefault(uUID, false);
    }

    private void check() {
        if (SettingsConfig.SERVER.STAFF_ACTIVITY.ENABLED) {
            long value =
                    BukkitAPI.parseDuration(SettingsConfig.SERVER.STAFF_ACTIVITY.AFK_THRESHOLD);
            long now = System.currentTimeMillis();
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                if ((player = (Player) iterator2.next()).hasPermission(PERMISSION)) {
                    UUID uniqueId = player.getUniqueId();
                    long value2 = this.lastActivity.computeIfAbsent(uniqueId, result -> now);
                    if (now - value2 >= value) {
                        this.afk.put(uniqueId, true);
                        IProfile profile;
                        if ((profile = this.plugin.getProfileHandler().getCachedProfile(uniqueId))
                                != null) {
                            profile.setAfkTime(
                                    profile.getAfkTime() + TimeUnit.SECONDS.toMillis(5L));
                        }
                    }
                }
            }
        }
    }
}
