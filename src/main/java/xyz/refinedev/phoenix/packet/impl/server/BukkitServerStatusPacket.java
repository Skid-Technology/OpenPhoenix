package xyz.refinedev.phoenix.packet.impl.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;

public class BukkitServerStatusPacket extends ServerStatusPacket {

    public BukkitServerStatusPacket() {}

    public BukkitServerStatusPacket(String text, String text2, String text3, boolean matches) {
        super(text, text2, text3, matches);
    }

    @Override
    public void onReceive() {
        String text =
                (super.online
                                ? MessageConfig.STAFF.SERVER_STARTED
                                : MessageConfig.STAFF.SERVER_STOPPED)
                        .replace("<server>", super.server)
                        .replace("<region>", super.region)
                        .replace("<time>", super.time);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Phoenix.getInstance()
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId())
                            .isSetting("staff-messages")
                    && player.hasPermission("core.admin")) {
                player.sendMessage(Color.translate(text));
            }
        }
        Bukkit.getConsoleSender().sendMessage(Color.translate(text));
    }
}
