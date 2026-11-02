package xyz.refinedev.phoenix.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.refinedev.lib.xseries.messages.Titles;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

public class RebootTask extends BukkitRunnable {

    private final Phoenix plugin;

    private long time;

    public void run() {
        switch (Math.toIntExact(this.time / 1000L)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 10:
            case 15:
            case 30:
            case 60:
            case 120:
            case 180:
            case 240:
            case 300:
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player;
                    Player player2 = player = (Player) iterator2.next();
                    player2.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player2, MessageConfig.REBOOT.REBOOTING_IN)
                                            .replace(
                                                    "<time>",
                                                    DateUtils.formatTimeMillis(this.time))));
                    if (MessageConfig.REBOOT.TITLE_ENABLED
                            && this.plugin.getNmsHandler().isRegistered()) {
                        Titles.sendTitle(
                                player,
                                5,
                                25,
                                5,
                                Color.translate(LocaleUtil.tr(player, MessageConfig.REBOOT.TITLE)),
                                Color.translate(
                                                LocaleUtil.tr(
                                                        player, MessageConfig.REBOOT.SUBTITLE))
                                        .replace("<time>", DateUtils.formatTimeMillis(this.time)));
                    }
                }
            default:
                if (this.time == 1000L) {
                    for (Player player3 : Bukkit.getOnlinePlayers()) {
                        player3.kickPlayer(
                                BukkitAPI.getKickMessage(
                                        LocaleUtil.tr(player3, MessageConfig.REBOOT.KICK)));
                    }
                } else if (this.time <= 0L) {
                    this.plugin.getServer().shutdown();
                }
                this.time -= 1000L;
        }
    }

    public RebootTask(Phoenix phoenix, long time) {
        this.plugin = phoenix;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }
}
