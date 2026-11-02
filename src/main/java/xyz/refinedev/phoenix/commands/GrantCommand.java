package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.grant.GrantRankDialog;
import xyz.refinedev.phoenix.menu.impl.grant.GrantRankMenu;
import xyz.refinedev.phoenix.profile.IProfile;

@Register(name = "grant")
@Requires("core.command.grant")
public class GrantCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, @Option({"disguise", "create", "multiple"}) IProfile profile) {
        GrantRankDialog grantRankDialog;
        if (!SettingsConfig.MODERN.DIALOGS.GRANT
                || (grantRankDialog =
                                this.plugin
                                        .getDialogHandler()
                                        .findPhoenixDialog(GrantRankDialog.class))
                        == null
                || !grantRankDialog.matchesEnabled(player, profile)) {
            this.plugin.getMenuHandler().openMenuAsync(new GrantRankMenu(profile), player);
        }
    }

    public GrantCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
