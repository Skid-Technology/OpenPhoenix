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
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "staffalert")
@Requires("core.command.staffalert")
public class StaffalertCommand {

    private final Phoenix plugin;

    private static final String text = "<message>";

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("message") @Text String text2) {
        this.plugin
                .getPacketHandler()
                .send(
                        new SendToStaffPacket(
                                MessageConfig.BASIC.ADMIN.STAFF_ALERT,
                                LocaleUtil.replacements(text, Color.translate(text2)),
                                false,
                                false));
    }

    public StaffalertCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
