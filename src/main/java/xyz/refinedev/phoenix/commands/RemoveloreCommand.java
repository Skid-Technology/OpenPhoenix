package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

import java.util.List;

@Register(name = "removelore")
@Requires("core.command.removelore")
public class RemoveloreCommand {

    private static final String text = "<id>";

    @Command(name = "")
    public void execute(@Sender Player player, @Named("number") int value) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) == null
                || itemStack.getType() == XMaterial.AIR.parseMaterial()) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
        ItemMeta itemMeta;
        if ((itemMeta = itemStack.getItemMeta()).hasLore() && value < itemMeta.getLore().size()) {
            List<String> values;
            (values = itemMeta.getLore()).remove(value);
            itemMeta.setLore(values);
            itemStack.setItemMeta(itemMeta);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.ITEM.LORE_REMOVE)
                                    .replace(text, String.valueOf(value))));
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(
                                    player, MessageConfig.BASIC.ADMIN.ITEM.LORE_REMOVE_FAILED)));
        }
    }
}
