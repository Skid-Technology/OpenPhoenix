package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "sudo")
@Requires("core.command.sudo")
public class SudoCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, Player player2, @Named("chat|command") @Text String text) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        IProfile profile2;
        if ((profile2 = this.plugin.getProfileHandler().getProfile(player2.getUniqueId()))
                        .getGeneralRank()
                        .getPriority()
                > profile.getGeneralRank().getPriority()) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SUDO.HIGHER_PRIORITY)
                            .replace("<player>", profile2.getNameWithColorNoDisguise()));
        }
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SUDO.SUDO)
                                .replace("<player>", profile2.getNameWithColorNoDisguise())
                                .replace("<chat>", text)));
        player2.chat(text);
    }

    public SudoCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
