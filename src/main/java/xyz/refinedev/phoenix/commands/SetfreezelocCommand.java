package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.ConfigLocation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "setfreezeloc",
        aliases = {"setsstp", "setfreezetp"})
@Requires("core.command.setfreezeloc")
public class SetfreezelocCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        SettingsConfig.FREEZE.TELEPORT.LOCATION = ConfigLocation.of(player.getLocation());
        SettingsConfig.INSTANCE.save();
        player.sendMessage(
                Color.translate(LocaleUtil.tr(player, MessageConfig.FREEZE.LOCATION_SET)));
    }

    public SetfreezelocCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
