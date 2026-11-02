package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;

@Register(name = "craft")
@Requires("core.command.craft")
public class CraftCommand {

    @Command(name = "")
    public void execute(@Sender Player player) {
        player.openWorkbench(player.getLocation(), true);
    }
}
