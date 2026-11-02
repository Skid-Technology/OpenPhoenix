package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "togglefriends",
        aliases = {"togglefriendrequests", "tfr"})
public class TogglefriendsCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        boolean setting =
                (profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        .isSetting("friend-requests");
        profile.updateMetadata("friend-requests", !setting, true);
        if (!setting) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_ON)
                                    .replace("<setting>", "friend requests")));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_OFF)
                                    .replace("<setting>", "friend requests")));
        }
    }

    public TogglefriendsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
