package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.handler.BukkitReportHandler;
import xyz.refinedev.phoenix.packet.impl.profile.ProfileMessagePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.UUID;

@Register(name = "reportmute", aliases = "requestmute")
@Requires("core.command.requestmute")
public class ReportmuteCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender CommandSender sender, @Option("disguise") IProfile profile) {
        UUID uniqueId = profile.getUniqueId();
        BukkitReportHandler bukkitReportHandler;
        if ((bukkitReportHandler = this.plugin.getReportHandler()).isReportMute(uniqueId)) {
            bukkitReportHandler.setRequestMute(false, uniqueId);
            this.plugin
                    .getPacketHandler()
                    .send(
                            new ProfileMessagePacket(
                                    uniqueId.toString(),
                                    this.plugin
                                            .getLocaleHandler()
                                            .get(
                                                    profile,
                                                    "request.unmute",
                                                    MessageConfig.REQUEST.UNMUTE)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.REQUEST.UNMUTE_SENDER)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        } else {
            bukkitReportHandler.setRequestMute(true, uniqueId);
            this.plugin
                    .getPacketHandler()
                    .send(
                            new ProfileMessagePacket(
                                    uniqueId.toString(),
                                    this.plugin
                                            .getLocaleHandler()
                                            .get(
                                                    profile,
                                                    "request.mute",
                                                    MessageConfig.REQUEST.MUTE)));
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.REQUEST.MUTE_SENDER)
                                    .replace("<player>", profile.getNameWithColorNoDisguise())));
        }
    }

    public ReportmuteCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
