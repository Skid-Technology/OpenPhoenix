package xyz.refinedev.phoenix.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "teleportworld", aliases = "tpworld")
@Requires("core.command.teleportworld")
public class TeleportworldCommand {

    private static final String text = "<world>";

    @Command(name = "")
    public void execute(@Sender Player player, World world) {
        if (world.getName().equalsIgnoreCase(player.getWorld().getName())) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                    player, MessageConfig.BASIC.ADMIN.TELEPORT.ALREADY_IN_WORLD)));
        }
        Phoenix.getInstance()
                .getNmsHandler()
                .getTeleportAPI()
                .teleport(
                        player, new Location(world, 0.0, world.getHighestBlockYAt(0, 0) + 30, 0.0));
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.TELEPORT.WORLD)
                                .replace(text, world.getName())));
    }
}
