package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;

@Register(name = "broadcast", aliases = "bc")
@Requires("core.command.broadcast")
public class BroadcastCommand {

    private static final String text = "<message>";

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("message") @Text String text2) {
        Bukkit.broadcastMessage(
                Color.translate(
                        MessageConfig.BASIC.ADMIN.BROADCAST.replace(text, Color.translate(text2))));
    }
}
