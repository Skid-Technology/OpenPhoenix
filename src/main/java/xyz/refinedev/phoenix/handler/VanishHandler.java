package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.staff.StaffStatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.NameTagUtils;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.events.player.PlayerVanishUpdateEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VanishHandler {

    private final Phoenix phoenix;

    private final String redisKey = "vanished";

    private Set<UUID> vanishedUsers = new HashSet<>();

    public void fetchUsers() {
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    HashSet values = new HashSet();
                    Map<String, String> entries = result.hgetall("vanished");
                    entries.forEach((result2, result3) -> values.add(UUID.fromString(result2)));
                    this.vanishedUsers = values;
                });
    }

    public boolean isVanished(Player player) {
        return this.vanishedUsers.contains(player.getUniqueId());
    }

    public boolean isVanished(UUID uUID) {
        return this.vanishedUsers.contains(uUID);
    }

    public void apply(UUID uUID, boolean matches) {
        if (matches) {
            this.vanishedUsers.add(uUID);
        } else {
            this.vanishedUsers.remove(uUID);
        }
    }

    public void updateVanish(Player player, boolean matches) {
        if (matches) {
            this.vanishedUsers.add(player.getUniqueId());
            player.setMetadata("vanished", new FixedMetadataValue(this.phoenix, true));
        } else {
            this.vanishedUsers.remove(player.getUniqueId());
            player.removeMetadata("vanished", this.phoenix);
        }
        this.phoenix
                .getPacketHandler()
                .send(
                        new StaffStatePacket(
                                StaffStatePacket.State.VANISH,
                                player.getUniqueId().toString(),
                                matches));
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.runRedisCommandAsync(
                result ->
                        matches
                                ? result.hset(
                                        "vanished",
                                        player.getUniqueId().toString(),
                                        player.getUniqueId().toString())
                                : result.hdel(
                                        "vanished",
                                        new String[] {player.getUniqueId().toString()}));
        Bukkit.getServer()
                .getPluginManager()
                .callEvent(new PlayerVanishUpdateEvent(player, matches));
    }

    public void vanishPlayerFor(Player player, Player player2) {
        IProfile profile = this.phoenix.getProfileHandler().getProfile(player2.getUniqueId());
        this.updateVanish(player, true);
        if (!profile.isSetting("show-staff")
                || !Phoenix.getInstance().getVisibilityHandler().canSee(player2, player)) {
            if (SettingsConfig.SERVER.NAMETAGS) {
                NameTagUtils.applyPlayerByVoid(player);
            }
            if (SettingsConfig.VANISH.UPDATE_PLAYER_VISIBILITY) {
                player2.hidePlayer(player);
            }
        }
    }

    public void vanishPlayer(Player player) {
        this.updateVanish(player, true);
        if (SettingsConfig.VANISH.UPDATE_PLAYER_VISIBILITY) {
            if (SettingsConfig.SERVER.NAMETAGS) {
                NameTagUtils.applyPlayerByVoid(player);
            }
            for (Player player2 : Utilities.getOnlinePlayers()) {
                if (!this.phoenix
                                .getProfileHandler()
                                .getProfile(player2.getUniqueId())
                                .isSetting("show-staff")
                        || !Phoenix.getInstance().getVisibilityHandler().canSee(player2, player)) {
                    player2.hidePlayer(player);
                }
            }
        }
    }

    public void unvanishPlayer(Player player) {
        this.updateVanish(player, false);
        if (SettingsConfig.VANISH.UPDATE_PLAYER_VISIBILITY) {
            if (SettingsConfig.SERVER.NAMETAGS) {
                NameTagUtils.applyPlayerByVoid(player);
            }
            for (Player player2 : Utilities.getOnlinePlayers()) {
                if (Phoenix.getInstance().getVisibilityHandler().canSee(player2, player)) {
                    player2.showPlayer(player);
                }
            }
        }
    }

    public VanishHandler(Phoenix phoenix2) {
        this.phoenix = phoenix2;
    }
}
