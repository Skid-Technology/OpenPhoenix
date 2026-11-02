package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

@Help
@Register(
        name = "item",
        aliases = {"give", "i"})
@Requires("core.command.item")
public class ItemCommand {

    @Command(name = "")
    public void execute(
            @Sender Player player,
            XMaterial xMaterial,
            @Default("1") @Named("amount") int value,
            @Optional Player player2) {
        if (xMaterial == XMaterial.AIR) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
        ItemStack itemStack;
        if ((itemStack = xMaterial.parseItem()) == null) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
        itemStack.setAmount(value);
        Player player3;
        (player3 = player2 != null ? player2 : player)
                .getInventory()
                .addItem(new ItemStack[] {itemStack});
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.ITEM.GAVE_OTHER)
                                .replace("<player>", player3.getName())
                                .replace("<amount>", String.valueOf(value))
                                .replace("<material>", itemStack.getType().name())));
    }
}
