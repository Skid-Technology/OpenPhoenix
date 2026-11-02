package xyz.refinedev.phoenix.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

@Register(name = "staffbroadcast")
@Requires("core.command.staffbroadcast")
public class StaffbroadcastCommand {

    @Command(name = "")
    public void execute(@Sender CommandSender sender, @Named("message") @Text String text) {
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next()).hasPermission("core.staff")) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.STAFF_BROADCAST)
                                        .replace("<message>", text)));
            }
        }
    }
}
