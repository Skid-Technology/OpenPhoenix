package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
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

@Register(name = "teleportall", aliases = "tpall")
@Requires("core.command.teleportall")
public class TeleportallCommand {

    @Command(name = "")
    public void execute(@Sender Player player) {
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            Phoenix.getInstance()
                    .getNmsHandler()
                    .getTeleportAPI()
                    .teleport(player2, player.getLocation());
        }
        player.sendMessage(
                Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.ALL)));
        BukkitAPI.sendAdminAlert(
                player, MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.TELEPORTED_EVERYONE_TO_HIMSELF);
    }
}
