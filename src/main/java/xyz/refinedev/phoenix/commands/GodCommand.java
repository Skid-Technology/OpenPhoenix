package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;

@Register(name = "god")
@Requires("core.command.god")
public class GodCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(@Sender Player player) {
        if (player.hasMetadata("GodMode")) {
            player.removeMetadata("GodMode", this.plugin);
        } else {
            player.setMetadata("GodMode", new FixedMetadataValue(this.plugin, true));
        }
        player.sendMessage(
                player.hasMetadata("GodMode")
                        ? Color.translate(MessageConfig.BASIC.ADMIN.GOD.ENABLED)
                        : Color.translate(MessageConfig.BASIC.ADMIN.GOD.DISABLED));
        BukkitAPI.sendAdminAlert(
                player,
                player.hasMetadata("GodMode")
                        ? MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.GOD_MODE_ON
                        : MessageConfig.BASIC.ADMIN.ADMIN_ALERTS.GOD_MODE_OFF);
    }

    public GodCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
