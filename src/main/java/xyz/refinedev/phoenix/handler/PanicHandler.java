package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.dialog.impl.StaffPanicDialog;
import xyz.refinedev.phoenix.packet.impl.staff.StaffStatePacket;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PanicHandler {

    private final Phoenix phoenix;

    private final String redisKey = "panic";

    private Set<UUID> frozenUsers = new HashSet<>();

    public void fetchUsers() {
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.executeRedisCommand(
                result -> {
                    HashSet values = new HashSet();
                    Map<String, String> entries = result.hgetall("panic");
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

    public void setPanic(UUID uUID, boolean matches) {
        Player player;
        if (!matches && (player = Bukkit.getPlayer(uUID)) != null) {
            this.phoenix
                    .getDialogHandler()
                    .findPhoenixDialog(StaffPanicDialog.class)
                    .applyPlayer(player);
        }
        this.phoenix
                .getPacketHandler()
                .send(new StaffStatePacket(StaffStatePacket.State.PANIC, uUID.toString(), matches));
        IDatabaseHandler databaseHandler = this.phoenix.getDatabaseHandler();
        databaseHandler.runRedisCommand(
                result -> {
                    if (matches) {
                        this.frozenUsers.add(uUID);
                        result.hset("panic", uUID.toString(), uUID.toString());
                    } else {
                        this.frozenUsers.remove(uUID);
                        result.hdel("panic", new String[] {uUID.toString()});
                    }
                    return null;
                });
    }

    public boolean isPanic(UUID uUID) {
        return this.frozenUsers.contains(uUID);
    }

    public Phoenix getPhoenix() {
        return this.phoenix;
    }

    public String getRedisKey() {
        return "panic";
    }

    public Set<UUID> getFrozenUsers() {
        return this.frozenUsers;
    }

    public PanicHandler(Phoenix phoenix2) {
        this.phoenix = phoenix2;
    }
}
