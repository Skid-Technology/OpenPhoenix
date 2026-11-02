package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(
        name = "clear",
        aliases = {"clearinv", "ci"})
@Requires("core.command.clear")
public class ClearCommand {

    private final Phoenix plugin;

    @Command(name = "")
    public void execute(
            @Sender Player player,
            @Optional @Requires("core.command.clear.others") Player player2) {
        if (player2 != null) {
            int value = this.calculateCount(player2);
            player2.getInventory().clear();
            player2.getInventory().setArmorContents(null);
            player2.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.CLEAR.OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player2.getUniqueId())
                                                    .getNameWithColor())
                                    .replace("<total>", String.valueOf(value))));
            player2.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player2, MessageConfig.BASIC.ADMIN.CLEAR.R_OTHER)
                                    .replace(
                                            "<player>",
                                            this.plugin
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId())
                                                    .getNameWithColor())
                                    .replace("<total>", String.valueOf(value))));
        } else {
            int value2 = this.calculateCount(player);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.CLEAR.SELF)
                                    .replace("<total>", String.valueOf(value2))));
        }
    }

    private int calculateCount(Player player) {
        int value = 0;
        ItemStack[] itemStacks;
        int value2 = (itemStacks = player.getInventory().getContents()).length;
        for (int index = 0; index < value2; index++) {
            ItemStack itemStack;
            if ((itemStack = itemStacks[index]) != null) {
                value += itemStack.getAmount();
            }
        }
        value2 = (itemStacks = player.getInventory().getArmorContents()).length;
        for (int index2 = 0; index2 < value2; index2++) {
            ItemStack itemStack2;
            if ((itemStack2 = itemStacks[index2]) != null) {
                value += itemStack2.getAmount();
            }
        }
        return value;
    }

    public ClearCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
