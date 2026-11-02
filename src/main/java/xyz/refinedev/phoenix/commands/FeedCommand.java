package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "feed", aliases = "eat")
@Requires("core.command.feed")
public class FeedCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, @Optional @Requires("core.command.feed.others") Player player2) {
        if (player2 != null) {
            player2.setFoodLevel(20);
            player2.setSaturation(0.6F);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FEED.OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player2.getUniqueId())
                                                    .getNameWithColorNoDisguise())));
            player2.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.FEED.R_OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId())
                                                    .getNameWithColorNoDisguise())));
            BukkitAPI.sendAdminAlert(
                    player,
                    MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.FED_SOMEONE.replace(
                            "<target>",
                            this.plugin
                                    .getProfileHandler()
                                    .getProfile(player2.getUniqueId())
                                    .getNameWithColorNoDisguise()));
        } else {
            player.setFoodLevel(20);
            player.setSaturation(0.6F);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FEED.SELF)));
            BukkitAPI.sendAdminAlert(player, MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.SELF_FED);
        }
    }

    public FeedCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
