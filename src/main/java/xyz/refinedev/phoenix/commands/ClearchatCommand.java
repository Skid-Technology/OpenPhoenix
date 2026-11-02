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
import xyz.refinedev.phoenix.packet.impl.server.SendToStaffPacket;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;
import java.util.stream.IntStream;

@Register(name = "clearchat")
@Requires("core.command.clearchat")
public class ClearchatCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(@Sender CommandSender sender) {
        String[] texts = new String[10];
        for (int index = 0; index < 9; index++) {
            texts[index] = Color.translate("&" + index);
        }
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission("core.clearchat.bypass")) {
                return;
            }
            IntStream intStream = IntStream.range(0, 1000);
            Player player2 = player;
            intStream.forEach(result -> player2.sendMessage(texts[result % 9]));
        }
        String text =
                sender instanceof Player
                        ? Color.translate(
                                this.plugin
                                        .getProfileHandler()
                                        .getProfile(((Player) sender).getUniqueId())
                                        .getNameWithColorNoDisguise())
                        : Color.translate(GlobalConfig.SERVER.CONSOLE_NAME);
        for (Player player3 : Bukkit.getOnlinePlayers()) {
            player3.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player3, MessageConfig.CHAT.CLEARED)
                                    .replace("<player>", text)));
        }
        Bukkit.getConsoleSender()
                .sendMessage(
                        Color.translate(
                                LocaleUtil.tr(Bukkit.getConsoleSender(), MessageConfig.CHAT.CLEARED)
                                        .replace("<player>", text)));
        this.plugin
                .getPacketHandler()
                .send(
                        new SendToStaffPacket(
                                MessageConfig.STAFF.CLEAR_CHAT_ALERT,
                                LocaleUtil.replacements(
                                        "<player>",
                                        text,
                                        "<server>",
                                        this.plugin.getNetworkHandler().getDisplayName()),
                                false,
                                true));
    }

    public ClearchatCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
