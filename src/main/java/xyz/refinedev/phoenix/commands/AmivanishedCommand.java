package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "amivanished", aliases = "amivis")
@Requires("core.command.vanish")
public class AmivanishedCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player) {
        if (this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()).isVanished()) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.STAFF.AM_I_VANISHED_TRUE)));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.STAFF.AM_I_VANISHED_FALSE)));
        }
    }

    public AmivanishedCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
