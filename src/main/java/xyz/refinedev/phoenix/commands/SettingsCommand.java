package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.impl.SettingsMenu;

@Register(name = "settings", aliases = "options")
public class SettingsCommand {

    private final Phoenix plugin;

    private static final String SETTINGS_TEXT = "settings";

    @Command(name = "")
    public void execute(@Sender Player player) {
        PhoenixDialog phoenixDialog;
        if (!SettingsConfig.MODERN.DIALOGS.SETTINGS
                || (phoenixDialog = this.plugin.getDialogHandler().findPhoenixDialog(SETTINGS_TEXT))
                        == null
                || !this.plugin.getDialogHandler().matchesEnabled(player, phoenixDialog, null)) {
            this.plugin.getMenuHandler().openMenu(new SettingsMenu(), player);
        }
    }

    public SettingsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
