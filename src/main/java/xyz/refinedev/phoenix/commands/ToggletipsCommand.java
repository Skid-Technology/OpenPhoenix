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

@Register(
        name = "toggletips",
        aliases = {"tips", "tt"})
@Requires("core.settings.tips")
public class ToggletipsCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        boolean setting =
                (profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        .isSetting("tips");
        profile.updateMetadata("tips", !setting, true);
        if (!setting) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_ON)
                                    .replace("<setting>", "tips")));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_OFF)
                                    .replace("<setting>", "tips")));
        }
    }

    public ToggletipsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
