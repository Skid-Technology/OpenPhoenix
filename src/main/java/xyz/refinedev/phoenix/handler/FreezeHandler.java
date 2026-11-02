package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffFreezeDialog;
import xyz.refinedev.phoenix.packet.impl.staff.StaffStatePacket;
import xyz.refinedev.phoenix.utils.events.player.PlayerFreezeEvent;
import xyz.refinedev.phoenix.utils.events.player.PlayerUnFreezeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FreezeHandler {

    private final Phoenix phoenix;

    private final String redisKey = "frozen";

    private Set<UUID> frozenUsers = new HashSet<>();

    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public void fetchUsers() {
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    HashSet values = new HashSet();
                    Map<String, String> entries = result.hgetall("frozen");
                    entries.forEach((result2, result3) -> values.add(UUID.fromString(result2)));
                    this.frozenUsers = values;
                });
    }

    public void apply(UUID uUID, boolean matches) {
        if (matches) {
            this.frozenUsers.add(uUID);
        } else {
            this.frozenUsers.remove(uUID);
        }
    }

    public void setFrozen(UUID uUID, boolean matches) {
        this.phoenix
                .getPacketHandler()
                .send(
                        new StaffStatePacket(
                                StaffStatePacket.State.FREEZE, uUID.toString(), matches));
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> {
                    if (matches) {
                        this.frozenUsers.add(uUID);
                        result.hset("frozen", uUID.toString(), uUID.toString());
                    } else {
                        this.frozenUsers.remove(uUID);
                        result.hdel("frozen", new String[] {uUID.toString()});
                    }
                    return null;
                });
        Player player;
        if ((player = Bukkit.getPlayer(uUID)) != null) {
            if (matches) {
                BukkitAPI.callEvent(new PlayerFreezeEvent(player, GlobalConfig.SERVER.NAME, -5));
            } else {
                this.phoenix
                        .getDialogHandler()
                        .findPhoenixDialog(StaffFreezeDialog.class)
                        .applyPlayer(player);
                BukkitAPI.callEvent(new PlayerUnFreezeEvent(player, GlobalConfig.SERVER.NAME));
            }
        }
    }

    public boolean isFrozen(UUID uUID) {
        return this.frozenUsers.contains(uUID);
    }

    public Phoenix getPhoenix() {
        return this.phoenix;
    }

    public String getRedisKey() {
        return "frozen";
    }

    public Set<UUID> getFrozenUsers() {
        return this.frozenUsers;
    }

    public Map<UUID, Location> getPreviousLocations() {
        return this.previousLocations;
    }

    public FreezeHandler(Phoenix phoenix2) {
        this.phoenix = phoenix2;
    }
}
