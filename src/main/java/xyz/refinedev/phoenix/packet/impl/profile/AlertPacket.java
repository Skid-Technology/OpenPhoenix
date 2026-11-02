package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;

import java.util.Optional;

public class AlertPacket implements Packet {

    private String message;

    @Override
    public void onReceive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Optional<XSound> optional = XSound.matchXSound(SettingsConfig.SERVER.SOUNDS.ALERT);
            optional.ifPresent(
                    result ->
                            player.playSound(
                                    player.getLocation(), result.parseSound(), 2.0F, 2.0F));
        }
        Bukkit.broadcastMessage(Color.translate(this.message));
    }

    public AlertPacket(String message) {
        this.message = message;
    }

    public AlertPacket() {}
}
