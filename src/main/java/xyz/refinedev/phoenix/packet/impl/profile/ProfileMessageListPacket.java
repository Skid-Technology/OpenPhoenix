package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;

import java.util.List;
import java.util.UUID;

public class ProfileMessageListPacket implements Packet {

    private UUID uniqueId;

    private List<String> messages;

    @Override
    public void onReceive() {
        Player player;
        if ((player = Bukkit.getPlayer(this.uniqueId)) != null) {
            for (String text : this.messages) {
                player.sendMessage(Color.translate(text));
            }
        }
    }

    public ProfileMessageListPacket(UUID uniqueId, List<String> messages) {
        this.uniqueId = uniqueId;
        this.messages = messages;
    }

    public ProfileMessageListPacket() {}
}
