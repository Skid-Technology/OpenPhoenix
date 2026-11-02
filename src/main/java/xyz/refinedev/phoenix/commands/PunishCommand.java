package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.impl.PunishMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.PunishmentUtil;

import java.util.concurrent.atomic.AtomicBoolean;

@Register(name = "punish")
@Requires("core.command.punish")
public class PunishCommand {

    private final Phoenix plugin;

    private static final String PUNISH_TEXT = "punish";

    @Command(name = "")
    public void execute(@Sender Player player, @Option({"disguise", "multiple"}) IProfile profile) {
        if (PunishmentUtil.canPunish(player, profile, new AtomicBoolean(false))) {
            if (!SettingsConfig.MODERN.DIALOGS.PUNISH
                    || !this.plugin
                            .getDialogHandler()
                            .matchesEnabled(
                                    player,
                                    this.plugin.getDialogHandler().findPhoenixDialog(PUNISH_TEXT),
                                    profile.getUniqueId())) {
                this.plugin.getMenuHandler().openMenuAsync(new PunishMenu(profile), player);
            }
        }
    }

    public PunishCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
