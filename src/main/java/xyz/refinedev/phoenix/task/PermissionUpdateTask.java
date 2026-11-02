package xyz.refinedev.phoenix.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffFreezeDialog;
import xyz.refinedev.phoenix.menu.impl.staff.FrozenMenu;

import java.util.Iterator;
import java.util.UUID;

public class PermissionUpdateTask implements Runnable {

    private final Phoenix plugin;

    @Override
    public void run() {
        if (SettingsConfig.FREEZE.MENU) {
            Iterator<UUID> iterator2 = this.plugin.getFreezeHandler().getFrozenUsers().iterator();
            while (iterator2.hasNext()) {
                Player player;
                if ((player = Bukkit.getPlayer((UUID) iterator2.next())) != null
                        && (!SettingsConfig.MODERN.DIALOGS.FREEZE
                                || !this.plugin
                                        .getDialogHandler()
                                        .findPhoenixDialog(StaffFreezeDialog.class)
                                        .matchesEnabled(player))) {
                    Phoenix.getInstance().getMenuHandler().openMenuSync(new FrozenMenu(), player);
                }
            }
        }
    }

    public PermissionUpdateTask(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
