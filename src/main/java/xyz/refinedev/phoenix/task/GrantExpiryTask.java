package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffPanicDialog;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;
import java.util.UUID;

public class GrantExpiryTask implements Runnable {

    private final Phoenix plugin;

    @Override
    public void run() {
        Iterator<UUID> iterator2 = this.plugin.getPanicHandler().getFrozenUsers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = Bukkit.getPlayer((UUID) iterator2.next())) != null
                    && (!SettingsConfig.MODERN.DIALOGS.PANIC
                            || !this.plugin
                                    .getDialogHandler()
                                    .findPhoenixDialog(StaffPanicDialog.class)
                                    .matchesEnabled(player))) {
                for (String text : LocaleUtil.tr(player, MessageConfig.PANIC.MESSAGE)) {
                    player.sendMessage(Color.translate(text));
                }
            }
        }
    }

    public GrantExpiryTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
