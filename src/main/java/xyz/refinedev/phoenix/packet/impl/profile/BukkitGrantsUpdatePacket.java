package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.utils.NameTagUtils;

import java.util.UUID;

public class BukkitGrantsUpdatePacket extends GrantsUpdatePacket {

    public BukkitGrantsUpdatePacket() {}

    public BukkitGrantsUpdatePacket(UUID uUID) {
        super(uUID);
    }

    @Override
    public void onReceive() {
        if (this.updateGrants() != null) {
            Player player;
            if ((player = Bukkit.getPlayer(super.uuid)) != null) {
                NameTagUtils.applyPlayer(player);
            }
        }
    }
}
