package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.List;

@Register(name = "help", aliases = "helpme")
public class HelpCommand {

    @Command(name = "")
    public void execute(@Sender CommandSender sender) {
        if (sender.hasPermission("core.admin")) {
            List<String> values = LocaleUtil.tr(sender, MessageConfig.BASIC.GENERAL.ADMIN_HELP);
            values.forEach(result -> sender.sendMessage(Color.translate(result)));
        }
        if (sender.hasPermission("core.staff")) {
            List<String> values2 = LocaleUtil.tr(sender, MessageConfig.BASIC.GENERAL.STAFF_HELP);
            values2.forEach(result -> sender.sendMessage(Color.translate(result)));
        }
        List<String> values3 = LocaleUtil.tr(sender, MessageConfig.BASIC.GENERAL.HELP);
        values3.forEach(result -> sender.sendMessage(Color.translate(result)));
    }
}
