package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.impl.profile.AlertPacket;

@Register(name = "alert")
@Requires("core.command.alert")
public class AlertCommand {

    private final Phoenix plugin;

    private static final String text = "<message>";

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("message") @Text String text2) {
        this.plugin
                .getPacketHandler()
                .send(new AlertPacket(MessageConfig.STAFF.ALERT.replace(text, text2)));
    }

    public AlertCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
