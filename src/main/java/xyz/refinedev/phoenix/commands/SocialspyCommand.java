package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "socialspy", aliases = "spy")
@Requires("core.command.socialspy")
public class SocialspyCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        boolean metadata =
                (profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        .isMetadata("social-spy", false);
        profile.updateMetadata("social-spy", !metadata, true);
        if (!metadata) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_ON)
                                    .replace("<setting>", "social spy")));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_OFF)
                                    .replace("<setting>", "social spy")));
        }
    }

    public SocialspyCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
