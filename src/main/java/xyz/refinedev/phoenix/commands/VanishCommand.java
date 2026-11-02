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

@Register(
        name = "vanish",
        aliases = {"v", "invis", "vis", "silent"})
@Requires("core.command.vanish")
public class VanishCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Optional @Requires("core.command.vanish.others") Player player2) {
        if (player2 != null) {
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getProfile(player2.getUniqueId()))
                    .isVanished()) {
                this.plugin.getVanishHandler().unvanishPlayer(player2);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.VANISH.DISABLED_OTHER)
                                        .replace(
                                                "<player>", profile.getNameWithColorNoDisguise())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.STAFF.ADMIN_ALERTS.VANISH_OTHER_OFF.replace(
                                "<target>", profile.getNameWithColorNoDisguise()));
            } else {
                this.plugin.getVanishHandler().vanishPlayer(player2);
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.VANISH.ENABLED_OTHER)
                                        .replace(
                                                "<player>", profile.getNameWithColorNoDisguise())));
                BukkitAPI.sendAdminAlert(
                        player,
                        MessageConfig.STAFF.ADMIN_ALERTS.VANISH_OTHER_ON.replace(
                                "<target>", profile.getNameWithColorNoDisguise()));
            }
        } else {
            if (this.plugin.getProfileHandler().getProfile(player.getUniqueId()).isVanished()) {
                this.plugin.getVanishHandler().unvanishPlayer(player);
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.DISABLED)));
                BukkitAPI.sendAdminAlert(player, MessageConfig.STAFF.ADMIN_ALERTS.SELF_VANISH_OFF);
            } else {
                this.plugin.getVanishHandler().vanishPlayer(player);
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.VANISH.ENABLED)));
                BukkitAPI.sendAdminAlert(player, MessageConfig.STAFF.ADMIN_ALERTS.SELF_VANISH_ON);
            }
        }
    }

    public VanishCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
