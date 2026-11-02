package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;

import java.util.UUID;

public class ProfileMessagePacket implements Packet {

    private String uniqueId;

    private String message;

    @Override
    public void onReceive() {
        Player player;
        if ((player = Bukkit.getPlayer(UUID.fromString(this.uniqueId))) != null) {
            player.sendMessage(Color.translate(this.message));
        }
    }

    public ProfileMessagePacket(String uniqueId, String message) {
        this.uniqueId = uniqueId;
        this.message = message;
    }

    public ProfileMessagePacket() {}
}
