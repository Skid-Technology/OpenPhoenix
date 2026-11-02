package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "mutechat")
@Requires("core.command.mutechat")
public class MutechatCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender CommandSender sender) {
        String text =
                sender instanceof Player
                        ? Color.translate(
                                this.plugin
                                        .getProfileHandler()
                                        .getProfile(((Player) sender).getUniqueId())
                                        .getNameWithColorNoDisguise())
                        : Color.translate(GlobalConfig.SERVER.CONSOLE_NAME);
        if (!SettingsConfig.CHAT_CONTROL.MUTED) {
            SettingsConfig.CHAT_CONTROL.MUTED = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.CHAT.MUTED)
                                        .replace("<player>", text)));
            }
            Bukkit.getConsoleSender()
                    .sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    Bukkit.getConsoleSender(),
                                                    MessageConfig.CHAT.MUTED)
                                            .replace("<player>", text)));
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.MUTE_CHAT_ALERT,
                                    LocaleUtil.replacements(
                                            "<player>",
                                            text,
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName()),
                                    false,
                                    true));
        } else {
            SettingsConfig.CHAT_CONTROL.MUTED = false;
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                player2.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player2, MessageConfig.CHAT.UNMUTED)
                                        .replace("<player>", text)));
            }
            Bukkit.getConsoleSender()
                    .sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    Bukkit.getConsoleSender(),
                                                    MessageConfig.CHAT.UNMUTED)
                                            .replace("<player>", text)));
            this.plugin
                    .getPacketHandler()
                    .send(
                            new SendToStaffPacket(
                                    MessageConfig.STAFF.UNMUTE_CHAT_ALERT,
                                    LocaleUtil.replacements(
                                            "<player>",
                                            text,
                                            "<server>",
                                            this.plugin.getNetworkHandler().getDisplayName()),
                                    false,
                                    true));
        }
        SettingsConfig.INSTANCE.save();
    }

    public MutechatCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
