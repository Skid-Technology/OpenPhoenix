package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.server.LocationSerilization;

@Register(name = "setspawn")
@Requires("core.command.setspawn")
public class SetspawnCommand {

    @Command(name = "")
    public void execute(@Sender Player player) {
        SettingsConfig.ON_JOIN.TELEPORT_LOCATION =
                LocationSerilization.serializeLocation(player.getLocation());
        SettingsConfig.INSTANCE.save();
        player.sendMessage(
                Color.translate(LocaleUtil.tr(player, MessageConfig.GENERAL.SPAWN_UPDATED)));
    }
}
