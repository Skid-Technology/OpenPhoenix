package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Help
@Register(name = "reboot")
@Requires("core.command.reboot")
public class RebootCommand {

    private final Phoenix plugin;

    private static final String text = "<time>";

    private static final long time = 5000L;

    @Command(name = "now")
    public void now(@Sender CommandSender sender) {
        if (this.plugin.getRebootHandler().isRebootScheduled()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.REBOOT.ALREADY_REBOOTING));
        }
        this.plugin.getRebootHandler().reboot(sender, time);
    }

    @Command(name = "status")
    public void status(@Sender CommandSender sender) {
        if (!this.plugin.getRebootHandler().isRebootScheduled()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.REBOOT.NOT_REBOOTING));
        }
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.REBOOT.REBOOTING_IN)
                                .replace(
                                        text,
                                        DateUtils.formatTimeMillis(
                                                this.plugin
                                                        .getRebootHandler()
                                                        .getTimeRemaining()))));
    }

    @Command(name = "schedule")
    public void schedule(
            @Sender CommandSender sender, @Named("time") @Option("temporary") Duration duration) {
        if (this.plugin.getRebootHandler().isRebootScheduled()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.REBOOT.ALREADY_REBOOTING));
        }
        this.plugin.getRebootHandler().reboot(sender, duration.getTimeTime());
    }

    @Command(name = "cancel")
    public void cancel(@Sender CommandSender sender) {
        if (!this.plugin.getRebootHandler().isRebootScheduled()) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.REBOOT.NOT_REBOOTING));
        }
        this.plugin.getRebootHandler().cancel(sender);
    }

    public RebootCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
