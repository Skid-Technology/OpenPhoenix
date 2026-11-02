package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.impl.SocialMediaMenu;
import xyz.refinedev.phoenix.profile.IProfile;

@Register(name = "socials", aliases = "socialmedia")
public class SocialsCommand {

    private final Phoenix plugin;

    private static final String SOCIALS_TEXT = "socials";

    @Command(name = "")
    public void execute(
            @Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        IProfile profile2 =
                this.plugin
                        .getProfileHandler()
                        .getProfile(profile != null ? profile.getUniqueId() : player.getUniqueId());
        PhoenixDialog phoenixDialog;
        if (profile != null
                || !SettingsConfig.MODERN.DIALOGS.SOCIALS
                || (phoenixDialog = this.plugin.getDialogHandler().findPhoenixDialog(SOCIALS_TEXT))
                        == null
                || !this.plugin.getDialogHandler().matchesEnabled(player, phoenixDialog, null)) {
            this.plugin
                    .getMenuHandler()
                    .openMenu(new SocialMediaMenu(profile2, profile == null), player);
        }
    }

    public SocialsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
