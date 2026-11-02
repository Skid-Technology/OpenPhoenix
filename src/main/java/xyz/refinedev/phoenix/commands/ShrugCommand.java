package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;

@Register(name = "shrug")
@Requires("core.command.shrug")
public class ShrugCommand {

    private static final String text = "\u00af\\_(\u30c4)_/\u00af";

    @Command(name = "")
    public void execute(@Sender Player player) {
        player.chat(text);
    }
}
