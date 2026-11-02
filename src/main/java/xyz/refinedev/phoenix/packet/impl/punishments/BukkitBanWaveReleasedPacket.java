package xyz.refinedev.phoenix.packet.impl.punishments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

public class BukkitBanWaveReleasedPacket extends BanWaveReleasedPacket {

    public BukkitBanWaveReleasedPacket(String text, int value) {
        super(text, value);
    }

    @Override
    public void onReceive() {
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            (player = (Player) iterator2.next()).sendMessage(this.render(player));
        }
        Bukkit.getConsoleSender().sendMessage(this.render(Bukkit.getConsoleSender()));
    }

    private String render(CommandSender sender) {
        return Color.translate(
                LocaleUtil.tr(sender, MessageConfig.BAN_WAVE.BROADCAST)
                        .replace("<wave>", super.wave)
                        .replace("<amount>", String.valueOf(super.amount)));
    }
}
