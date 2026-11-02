package xyz.refinedev.phoenix.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "top")
@Requires("core.command.top")
public class TopCommand {

    @Command(name = "")
    public void execute(@Sender Player player) {
        Location location;
        int highestBlockYAt =
                (location = player.getLocation())
                        .getWorld()
                        .getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
        if (location.getBlockY() >= highestBlockYAt) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                    player, MessageConfig.BASIC.ADMIN.TELEPORT.ALREADY_AT_TOP)));
        } else {
            Phoenix.getInstance()
                    .getNmsHandler()
                    .getTeleportAPI()
                    .teleport(
                            player,
                            new Location(
                                    location.getWorld(),
                                    location.getX(),
                                    highestBlockYAt + 1.0,
                                    location.getZ(),
                                    location.getYaw(),
                                    location.getPitch()));
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.TOP)));
        }
    }
}
