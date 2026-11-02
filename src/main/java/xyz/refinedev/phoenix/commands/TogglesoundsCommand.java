package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.xseries.XSound;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Optional;

@Register(
        name = "togglesounds",
        aliases = {"tpms", "sounds"})
public class TogglesoundsCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender Player player) {
        IProfile profile;
        boolean setting =
                (profile = this.platform.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        .isSetting("private-message-sounds");
        profile.updateMetadata("private-message-sounds", !setting, true);
        if (!setting) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_ON)
                                    .replace("<setting>", "private message sounds")));
            Optional<XSound> optional =
                    XSound.matchXSound(SettingsConfig.SERVER.SOUNDS.PRIVATE_MESSAGE);
            optional.ifPresent(
                    result ->
                            player.playSound(
                                    player.getLocation(), result.parseSound(), 2.0F, 2.0F));
        } else {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.TOGGLE_SETTING_OFF)
                                    .replace("<setting>", "private message sounds")));
        }
    }

    public TogglesoundsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
