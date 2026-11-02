package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "teleporthere",
        aliases = {"tphere", "tph", "s"})
@Requires("core.command.teleporthere")
public class TeleporthereCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, Player player2) {
        String nameWithColorNoDisguise =
                this.plugin
                        .getProfileHandler()
                        .getProfile(player2.getUniqueId())
                        .getNameWithColorNoDisguise();
        String nameWithColor =
                this.plugin.getProfileHandler().getProfile(player.getUniqueId()).getNameWithColor();
        this.plugin.getNmsHandler().getTeleportAPI().teleport(player2, player.getLocation());
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.HERE)
                                .replace("<target>", nameWithColorNoDisguise)));
        player2.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.TELEPORT.R_HERE)
                                .replace("<player>", nameWithColor)));
        BukkitAPI.sendAdminAlert(
                player,
                MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.TELEPORT_HERE.replace(
                        "<target>", nameWithColorNoDisguise));
    }

    public TeleporthereCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
