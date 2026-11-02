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
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "fly")
@Requires("core.command.fly")
public class FlyCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player, @Optional @Requires("core.command.fly.others") Player player2) {
        if (player2 != null) {
            IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
            IProfile profile2 = this.plugin.getProfileHandler().getProfile(player2.getUniqueId());
            if (player2.getAllowFlight()) {
                player2.setAllowFlight(false);
                player2.setFlying(false);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FLY.OTHER_OFF)
                                        .replace("<player>", profile2.getNameWithColor())));
                player2.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.FLY.R_OTHER_OFF)
                                        .replace("<player>", profile.getNameWithColor())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.FLIGHT_OTHER_OFF.replace(
                                "<target>", profile2.getNameWithColorNoDisguise()));
            } else {
                player2.setAllowFlight(true);
                player2.setFlying(true);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FLY.OTHER_ON)
                                        .replace("<player>", profile2.getNameWithColor())));
                player2.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.FLY.R_OTHER_ON)
                                        .replace("<player>", profile.getNameWithColor())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.FLIGHT_OTHER_ON.replace(
                                "<target>", profile2.getNameWithColorNoDisguise()));
            }
        } else if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FLY.DISABLED)));
            BukkitAPI.sendAdminAlert(
                    player, MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.SELF_FLIGHT_OFF);
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.FLY.ENABLED)));
            BukkitAPI.sendAdminAlert(player, MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.SELF_FLIGHT_ON);
        }
    }

    public FlyCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
