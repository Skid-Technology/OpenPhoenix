package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.api.IPacketHandler;
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "slowchat")
@Requires("core.command.slowchat")
public class SlowchatCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("seconds") int value) {
        if (value < 0) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.ERROR.POSITIVE_NUMBER));
        }
        String text =
                sender instanceof Player
                        ? Color.translate(
                                this.plugin
                                        .getProfileHandler()
                                        .getProfile(((Player) sender).getUniqueId())
                                        .getNameWithColorNoDisguise())
                        : Color.translate(GlobalConfig.SERVER.CONSOLE_NAME);
        SettingsConfig.CHAT_CONTROL.SLOW_DELAY = value;
        SettingsConfig.INSTANCE.save();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.CHAT.SLOWED)
                                    .replace("<player>", text)
                                    .replace("<seconds>", String.valueOf(value))));
        }
        Bukkit.getConsoleSender()
                .sendMessage(
                        Color.translate(
                                LocaleUtil.tr(Bukkit.getConsoleSender(), MessageConfig.CHAT.SLOWED)
                                        .replace("<player>", text)
                                        .replace("<seconds>", String.valueOf(value))));
        IPacketHandler packetHandler = this.plugin.getPacketHandler();
        String text2 = MessageConfig.STAFF.SLOW_CHAT_ALERT;
        String[] texts = new String[6];
        texts[0] = "<player>";
        texts[1] = text;
        texts[2] = "<seconds>";
        texts[3] = String.valueOf(value);
        texts[4] = "<server>";
        texts[5] = this.plugin.getNetworkHandler().getDisplayName();
        packetHandler.send(
                new SendToStaffPacket(text2, LocaleUtil.replacements(texts), false, true));
    }

    public SlowchatCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
