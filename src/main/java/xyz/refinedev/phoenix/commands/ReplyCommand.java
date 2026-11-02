package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "reply", aliases = "r")
public class ReplyCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player, @Named("message") @Text String text) {
        IProfile profile;
        if (!(profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                .hasMetadata("replyTo")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.GENERAL.MESSAGE.NO_ONE_TO_REPLY));
        }
        player.performCommand("message " + profile.getSetting("replyTo") + " " + text);
    }

    public ReplyCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
