package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.packet.impl.profile.AlertPacket;

@Register(name = "rawalert", aliases = "ra")
@Requires("core.command.rawalert")
public class RawalertCommand {

    private final CommonPlatform platform;

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("message") @Text String text) {
        this.platform.getPacketHandler().send(new AlertPacket(text));
    }

    public RawalertCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
