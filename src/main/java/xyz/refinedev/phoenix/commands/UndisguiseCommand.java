package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "undisguise",
        aliases = {"ud", "undis", "unnick"})
@Requires("core.command.undisguise")
public class UndisguiseCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        if (!(profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .getDisguiseData()
                .isDisguised()) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.DISGUISE.NOT_DISGUISED));
        }
        this.plugin.getDisguiseHandler().undisguise(profile, true);
    }

    public UndisguiseCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
