package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.impl.ProfileMenu;
import xyz.refinedev.phoenix.profile.IProfile;

@Register(name = "profile")
public class ProfileCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        IProfile profile2 =
                this.plugin
                        .getProfileHandler()
                        .getProfile(profile != null ? profile.getUniqueId() : player.getUniqueId());
        this.plugin.getMenuHandler().openMenu(new ProfileMenu(profile2, profile != null), player);
    }

    public ProfileCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
