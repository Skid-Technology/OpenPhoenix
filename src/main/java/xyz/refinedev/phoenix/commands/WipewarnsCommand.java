package xyz.refinedev.phoenix.commands;

import org.bukkit.command.ConsoleCommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.punishment.PunishmentMessages;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

@Register(name = "wipewarns", aliases = "wipeallwarns")
@Requires("core.command.wipewarns")
public class WipewarnsCommand {

    private final CommonPlatform platform;

    private static final String text = "/wipewarns";

    @Command(name = "", async = true)
    public void execute(
            @Sender ConsoleCommandSender consoleCommandSender, @Flag("confirm") boolean matches) {
        PunishmentMessages.sendMessage(
                this.platform,
                consoleCommandSender,
                PunishmentType.WARN,
                MessageConfig.PUNISHMENT.WARNS_WIPED,
                text,
                matches);
    }

    public WipewarnsCommand(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
