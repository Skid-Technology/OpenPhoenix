package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.menu.impl.staff.PlayerInventoryMenu;

@Register(name = "invsee", aliases = "openinv")
@Requires("core.command.invsee")
public class InvseeCommand {

    @Command(name = "")
    public void execute(@Sender Player player, Player player2) {
        Phoenix.getInstance().getMenuHandler().openMenu(new PlayerInventoryMenu(player2), player);
    }
}
