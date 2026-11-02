package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.report.RequestMessagePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "request", aliases = "helpop")
@Requires("core.command.request")
public class RequestCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender Player player, @Named("message") @Text String text) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        if (this.plugin.getReportHandler().isReportMute(player.getUniqueId())) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.REQUEST.MUTED));
        }
        if (profile.hasLocalCooldown("request") && !player.hasPermission("core.cooldowns.bypass")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.COOLDOWNS.REQUEST_COOLDOWN)
                            .replace(
                                    "<remaining>",
                                    DateUtils.formatTimeMillis(
                                            profile.getLocalRemaining("request"))));
        }
        player.sendMessage(Color.translate(LocaleUtil.tr(player, MessageConfig.REQUEST.SENT)));
        this.plugin
                .getPacketHandler()
                .send(
                        new RequestMessagePacket(
                                profile.getUuid(),
                                this.plugin.getNetworkHandler().getDisplayName(),
                                Color.stripColor(text)));
        this.plugin
                .getReportHandler()
                .applyString(
                        player.getName(),
                        this.plugin.getNetworkHandler().getDisplayName(),
                        Color.stripColor(text));
        this.plugin
                .getCooldownHandler()
                .placeOnCooldown(
                        "request",
                        player.getUniqueId(),
                        BukkitAPI.getDuration(SettingsConfig.COOLDOWNS.REQUEST));
    }

    public RequestCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
