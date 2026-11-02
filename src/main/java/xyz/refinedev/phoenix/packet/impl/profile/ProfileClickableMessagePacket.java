package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.UUID;

public class ProfileClickableMessagePacket implements Packet {

    private String uniqueId;

    private String message;

    private String hover;

    private String command;

    @Override
    public void onReceive() {
        Player player;
        if ((player = Bukkit.getPlayer(UUID.fromString(this.uniqueId))) != null) {
            Clickable clickable;
            (clickable = new Clickable()).add(this.message, this.hover, this.command);
            clickable.sendToPlayer(player);
        }
    }

    public ProfileClickableMessagePacket(
            String uniqueId, String message, String hover, String command) {
        this.uniqueId = uniqueId;
        this.message = message;
        this.hover = hover;
        this.command = command;
    }

    public ProfileClickableMessagePacket() {}
}
