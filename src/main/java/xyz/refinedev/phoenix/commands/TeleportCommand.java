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

@Register(name = "teleport", aliases = "tp")
@Requires("core.command.teleport")
public class TeleportCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player, Player player2) {
        String nameWithColorNoDisguise =
                this.plugin
                        .getProfileHandler()
                        .getProfile(player2.getUniqueId())
                        .getNameWithColorNoDisguise();
        this.plugin.getNmsHandler().getTeleportAPI().teleport(player, player2.getLocation());
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.TP)
                                .replace("<player>", nameWithColorNoDisguise)));
        BukkitAPI.sendAdminAlert(
                player,
                MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.TELEPORTED_TO.replace(
                        "<target>", nameWithColorNoDisguise));
    }

    public TeleportCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
