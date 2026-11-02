package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "build", aliases = "buildmode")
@Requires("core.command.build")
public class BuildCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Optional @Requires("core.command.build.others") Player player2) {
        if (!SettingsConfig.BASICS.BUILDMODE) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                    player, MessageConfig.BASIC.ADMIN.BUILD.DISABLED_IN_CONFIG)));
        }
        if (player2 == null) {
            if (player.hasMetadata("BuildMode")) {
                player.removeMetadata("BuildMode", this.plugin);
            } else {
                player.setMetadata("BuildMode", new FixedMetadataValue(this.plugin, true));
            }
            player.sendMessage(
                    player.hasMetadata("BuildMode")
                            ? Color.translate(MessageConfig.BASIC.ADMIN.BUILD.ENABLED)
                            : Color.translate(MessageConfig.BASIC.ADMIN.BUILD.DISABLED));
        } else {
            IProfile profile = this.plugin.getProfileHandler().getProfile(player2.getUniqueId());
            if (player2.hasMetadata("BuildMode")) {
                player2.removeMetadata("BuildMode", this.plugin);
            } else {
                player2.setMetadata("BuildMode", new FixedMetadataValue(this.plugin, true));
            }
            player2.sendMessage(
                    player2.hasMetadata("BuildMode")
                            ? Color.translate(MessageConfig.BASIC.ADMIN.BUILD.ENABLED)
                            : Color.translate(MessageConfig.BASIC.ADMIN.BUILD.DISABLED));
            player.sendMessage(
                    player2.hasMetadata("BuildMode")
                            ? Color.translate(
                                    MessageConfig.BASIC.ADMIN.BUILD.ENABLED_OTHER.replace(
                                            "<player>", profile.getNameWithColorNoDisguise()))
                            : Color.translate(
                                    MessageConfig.BASIC.ADMIN.BUILD.DISABLED_OTHER.replace(
                                            "<player>", profile.getNameWithColorNoDisguise())));
        }
    }

    public BuildCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
