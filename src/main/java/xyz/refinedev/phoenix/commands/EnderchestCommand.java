package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "enderchest", aliases = "ec")
@Requires("core.command.enderchest")
public class EnderchestCommand {

    private final Phoenix plugin;

    private static final String text = "<player>";

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Optional @Requires("core.command.enderchest.other") Player player2) {
        if (player2 != null) {
            player.openInventory(player2.getEnderChest());
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.ENDER_CHEST_OTHER)
                                    .replace(text, player2.getName())));
        } else {
            player.openInventory(player.getEnderChest());
        }
    }

    public EnderchestCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
