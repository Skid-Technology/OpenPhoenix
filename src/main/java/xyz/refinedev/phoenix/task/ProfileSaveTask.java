package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;
import java.util.UUID;

public class ProfileSaveTask implements Runnable {

    private final Phoenix plugin;

    @Override
    public void run() {
        Iterator<UUID> iterator2 = this.plugin.getFreezeHandler().getFrozenUsers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = Bukkit.getPlayer((UUID) iterator2.next())) != null) {
                for (String text : LocaleUtil.tr(player, MessageConfig.FREEZE.MESSAGE)) {
                    player.sendMessage(Color.translate(text));
                }
            }
        }
    }

    public ProfileSaveTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
